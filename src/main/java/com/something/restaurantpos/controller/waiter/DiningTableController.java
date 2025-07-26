package com.something.restaurantpos.controller.waiter;

import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.service.IDiningTableService;
import com.something.restaurantpos.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/waiter/tables")
@RequiredArgsConstructor
public class DiningTableController {

    private final IDiningTableService diningTableService;
    private final IOrderService orderService;

    @GetMapping
    public String showTables(Model model) {
        List<DiningTable> tables = diningTableService.findAll();
        model.addAttribute("tables", tables);
        return "pages/waiter/tables-management";
    }

    @PostMapping("/{id}/change-status")
    public String changeStatus(@PathVariable Integer id,
                               @RequestParam DiningTable.TableStatus status,
                               RedirectAttributes redirect) {
        DiningTable table = diningTableService.findById(id);
        boolean hasUnpaidOrder = orderService.existsOpenOrderByTableId(id);

        if (table.getStatus() == DiningTable.TableStatus.SERVING && hasUnpaidOrder) {
            redirect.addFlashAttribute("error", "Bàn đang phục vụ và có món chưa thanh toán. Không thể đổi trạng thái.");
        } else {
            table.setStatus(status);
            diningTableService.save(table);
            redirect.addFlashAttribute("success", "Cập nhật trạng thái thành công.");
        }

        return "redirect:/waiter/tables";
    }

    @PostMapping("/{id}/force-serving")
    public String forceChangeToServing(@PathVariable Integer id, RedirectAttributes redirect) {
        DiningTable table = diningTableService.findById(id);
        if (table != null && table.getStatus() != DiningTable.TableStatus.SERVING) {
            table.setStatus(DiningTable.TableStatus.SERVING);
            diningTableService.save(table);
        }

        Optional<Order> openOrder = orderService.findOpenOrderByTableId(id);
        if (openOrder.isEmpty()) {
            orderService.createNewOrderForTable(table);
        }

        return "redirect:/waiter/order?tableId=" + id;
    }

    @GetMapping("/go-to-order")
    public String goToOrder(@RequestParam Integer tableId, RedirectAttributes redirect) {
        DiningTable table = diningTableService.findById(tableId);
        if (table == null) {
            redirect.addFlashAttribute("error", "Không tìm thấy bàn.");
            return "redirect:/waiter/tables";
        }
        if (table.getStatus() != DiningTable.TableStatus.SERVING) {
            redirect.addFlashAttribute("error", "Bàn chưa ở trạng thái Đang phục vụ.");
            return "redirect:/waiter/tables";
        }

        Optional<Order> openOrder = orderService.findOpenOrderByTableId(tableId);
        if (openOrder.isEmpty()) {
            orderService.createNewOrderForTable(table);
        }

        return "redirect:/waiter/order?tableId=" + tableId;
    }
}
