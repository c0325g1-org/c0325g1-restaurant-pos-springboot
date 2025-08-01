package com.something.restaurantpos.controller;

import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.service.IDiningTableService;
import com.something.restaurantpos.service.IInvoiceService;
import com.something.restaurantpos.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/cashier/tables")
@RequiredArgsConstructor
public class CashierTableController {

        private final IDiningTableService diningTableService;
        private final IOrderService orderService;
        private final IInvoiceService invoiceService;
        @GetMapping
        public String listTables(Model model) {
            model.addAttribute("tables", diningTableService.findAll());
            return "pages/cashier/table-list";
        }
        @PostMapping("/checkout")
        public String checkoutTable(@RequestParam("tableId") Integer tableId, RedirectAttributes redirect) {
            Optional<Order> orderOpt = orderService.findLastedOpenOrderByTableId(tableId);
            if (orderOpt.isEmpty()) {
                redirect.addFlashAttribute("errorMessage", "Không tìm thấy order đang mở cho bàn.");
                return "redirect:/cashier/tables";
            }
            Invoice invoice;
            try {
                invoice = invoiceService.createInvoiceFromOrder(orderOpt.get());
            } catch (Exception ex) {
                invoice = invoiceService.findByOrderId(orderOpt.get().getId());
            }
            return "redirect:/cashier/" + invoice.getId() + "/invoice";
        }
}
