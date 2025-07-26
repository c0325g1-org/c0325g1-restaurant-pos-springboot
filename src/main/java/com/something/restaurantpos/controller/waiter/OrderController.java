package com.something.restaurantpos.controller.waiter;

import com.something.restaurantpos.dto.MenuItemDTO;
import com.something.restaurantpos.dto.OrderCartDTO;
import com.something.restaurantpos.dto.OrderItemDTO;
import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.mapper.MenuItemMapper;
import com.something.restaurantpos.service.IDiningTableService;
import com.something.restaurantpos.service.IMenuCategoryService;
import com.something.restaurantpos.service.IMenuItemService;
import com.something.restaurantpos.service.IOrderService;
import com.something.restaurantpos.util.OrderCartUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/waiter/order")
@SessionAttributes("cartMap")
@RequiredArgsConstructor
public class OrderController {

    private final IMenuItemService menuItemService;
    private final IMenuCategoryService menuCategoryService;
    private final IDiningTableService diningTableService;
    private final IOrderService orderService;
    private final MenuItemMapper menuItemMapper;

    @ModelAttribute("cartMap")
    public Map<Integer, OrderCartDTO> initCartMap() {
        return new HashMap<>();
    }

    @GetMapping
    public String showOrderPage(@RequestParam Integer tableId,
                                @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap,
                                Model model) {
        cartMap.putIfAbsent(tableId, new OrderCartDTO(tableId));
        OrderCartDTO cart = cartMap.get(tableId);

        List<MenuItemDTO> menuItemDTOs = menuItemService.getAvailableItems().stream()
                .map(menuItemMapper::toDto)
                .toList();

        model.addAttribute("menuItems", menuItemDTOs);
        model.addAttribute("categories", menuCategoryService.findAll());
        model.addAttribute("tables", diningTableService.findAvailableTables());
        model.addAttribute("cart", cart);
        model.addAttribute("tableId", tableId);

        return "pages/waiter/order-page";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Integer tableId,
                                       @RequestParam Integer menuItemId,
                                       @RequestParam Integer quantity,
                                       @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap) {
        cartMap.putIfAbsent(tableId, new OrderCartDTO(tableId));
        OrderCartDTO cart = cartMap.get(tableId);
        if (cart == null) return ResponseEntity.badRequest().body(Map.of("error", "Bàn không hợp lệ"));

        MenuItem menu = menuItemService.getById(menuItemId);
        OrderItemDTO item = new OrderItemDTO(menu.getId(), menu.getName(), quantity, menu.getPrice());
        OrderCartUtils.addItem(cart, item);

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/update-quantity")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(@RequestParam Integer tableId,
                                            @RequestParam Integer menuItemId,
                                            @RequestParam Integer delta,
                                            @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap) {
        OrderCartDTO cart = cartMap.get(tableId);
        if (cart == null) return ResponseEntity.badRequest().body(Map.of("error", "Bàn không hợp lệ"));

        for (OrderItemDTO item : cart.getItems()) {
            if (item.getMenuItemId().equals(menuItemId)) {
                item.setQuantity(Math.max(1, item.getQuantity() + delta));
                break;
            }
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public ResponseEntity<?> removeItemFromCart(@RequestParam Integer tableId,
                                                @RequestParam Integer menuItemId,
                                                @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap) {
        OrderCartDTO cart = cartMap.get(tableId);
        if (cart != null) {
            OrderCartUtils.removeItem(cart, menuItemId);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/view")
    public String viewCartFragment(@RequestParam Integer tableId,
                                   @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap,
                                   Model model) {
        OrderCartDTO cart = cartMap.get(tableId);
        model.addAttribute("cart", cart);
        return "fragments/waiter/cart-fragment :: fragment";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam Integer tableId,
                              @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap,
                              RedirectAttributes redirect) {
        OrderCartDTO cart = cartMap.get(tableId);
        if (cart == null || cart.getItems().isEmpty()) {
            redirect.addFlashAttribute("error", "Không có món nào để gửi.");
            return "redirect:/waiter/order?tableId=" + tableId;
        }

        Integer employeeId = 1; // TODO: Replace with logged-in employee

        Optional<Order> existingOrderOpt = orderService.findOpenOrderByTableId(tableId);
        Order order;
        if (existingOrderOpt.isPresent()) {
            order = orderService.appendItemsToExistingOrder(existingOrderOpt.get(), cart);
        } else {
            order = orderService.placeOrder(cart, employeeId);
        }

        OrderCartUtils.clear(cart);

        redirect.addFlashAttribute("success", "Đã gửi món cho bàn " + order.getTable().getName());
        return "redirect:/waiter/order?tableId=" + tableId;
    }

    @GetMapping("/called-items")
    public ResponseEntity<List<OrderItem>> getCalledItems(@RequestParam Integer tableId) {
        return ResponseEntity.ok(orderService.getOrderItemsByTableAndStatuses(
                tableId, List.of(OrderItem.ItemStatus.NEW, OrderItem.ItemStatus.COOKING, OrderItem.ItemStatus.READY, OrderItem.ItemStatus.CANCELLED)
        ));
    }

    @GetMapping("/served-items")
    public ResponseEntity<List<OrderItem>> getServedItems(@RequestParam Integer tableId) {
        return ResponseEntity.ok(orderService.getOrderItemsByTableAndStatuses(
                tableId, List.of(OrderItem.ItemStatus.SERVED)
        ));
    }

    @PostMapping("/cancel-item")
    public ResponseEntity<Void> cancelOrderItem(@RequestParam Integer id) {
        orderService.updateItemStatus(id, OrderItem.ItemStatus.CANCELLED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/serve-item")
    public ResponseEntity<Void> serveOrderItem(@RequestParam Integer id) {
        orderService.updateItemStatus(id, OrderItem.ItemStatus.SERVED);
        return ResponseEntity.ok().build();
    }
}
