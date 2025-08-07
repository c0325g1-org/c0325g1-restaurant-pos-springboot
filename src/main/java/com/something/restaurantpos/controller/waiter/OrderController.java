package com.something.restaurantpos.controller.waiter;

import com.something.restaurantpos.dto.MenuItemDTO;
import com.something.restaurantpos.dto.OrderCartDTO;
import com.something.restaurantpos.dto.OrderItemDTO;
import com.something.restaurantpos.entity.*;
import com.something.restaurantpos.mapper.MenuItemMapper;
import com.something.restaurantpos.service.*;
import com.something.restaurantpos.util.CurrentUserUtil;
import com.something.restaurantpos.util.OrderCartUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/waiter/order")
@SessionAttributes("cartMap")
@RequiredArgsConstructor
public class OrderController {

    private final IMenuItemService menuItemService;
    private final IMenuCategoryService menuCategoryService;
    private final IDiningTableService diningTableService;
    private final IOrderService orderService;
    private final IOrderItemService orderItemService;
    private final MenuItemMapper menuItemMapper;

    @ModelAttribute("cartMap")
    public Map<Integer, OrderCartDTO> initCartMap() {
        return new HashMap<>();
    }

    @GetMapping
    public String showOrderPage(@RequestParam Integer tableId,
                                @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap,
                                Model model, RedirectAttributes redirectAttributes) {
        DiningTable table = diningTableService.findById(tableId);
        if (table == null || table.getStatus() != DiningTable.TableStatus.SERVING) {
            redirectAttributes.addFlashAttribute("errorMessage", table == null ?
                    "Không tìm thấy bàn." : "Bàn chưa ở trạng thái Đang phục vụ.");
            return "redirect:/waiter/tables";
        }
        Employee employee = CurrentUserUtil.getCurrentEmployee();
        cartMap.putIfAbsent(tableId, new OrderCartDTO(tableId));

        model.addAttribute("menuItems", getAvailableMenuItems());
        model.addAttribute("categories", menuCategoryService.findAll());
        model.addAttribute("tables", diningTableService.findAvailableTables());
        model.addAttribute("cart", cartMap.get(tableId));
        model.addAttribute("tableId", tableId);

        return "pages/waiter/order-page";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCart(@RequestParam Integer tableId,
                                       @RequestParam Integer menuItemId,
                                       @RequestParam Integer quantity,
                                       @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap) {
        OrderCartDTO cart = cartMap.computeIfAbsent(tableId, OrderCartDTO::new);

        MenuItem menu = menuItemService.getById(menuItemId);
        if (menu == null) return ResponseEntity.badRequest().body(Map.of("error", "Món không tồn tại"));

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

        cart.getItems().stream()
                .filter(item -> item.getMenuItemId().equals(menuItemId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(Math.max(1, item.getQuantity() + delta)));

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public ResponseEntity<?> removeItemFromCart(@RequestParam Integer tableId,
                                                @RequestParam Integer menuItemId,
                                                @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap) {
        Optional.ofNullable(cartMap.get(tableId))
                .ifPresent(cart -> OrderCartUtils.removeItem(cart, menuItemId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cart/view")
    public String viewCartFragment(@RequestParam Integer tableId,
                                   @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap,
                                   Model model) {
        OrderCartDTO cart = cartMap.getOrDefault(tableId, new OrderCartDTO(tableId));
        cart.setTableId(tableId);
        model.addAttribute("cart", cart);
        return "fragments/waiter/cart-fragment :: fragment";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam Integer tableId,
                              @ModelAttribute("cartMap") Map<Integer, OrderCartDTO> cartMap,
                              RedirectAttributes redirect) {
        OrderCartDTO cart = cartMap.get(tableId);
        if (cart == null || cart.getItems().isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Không có món nào để gửi.");
            return "redirect:/waiter/order?tableId=" + tableId;
        }

        Employee employee = CurrentUserUtil.getCurrentEmployee();

        Order order = orderService.findLastedOpenOrderByTableId(tableId)
                .map(existing -> orderService.appendItemsToExistingOrder(existing, cart))
                .orElseGet(() -> orderService.placeOrder(cart, Objects.requireNonNull(employee).getId()));

        OrderCartUtils.clear(cart);
        redirect.addFlashAttribute("successMessage", "Gọi món thành công!");
        return "redirect:/waiter/order?tableId=" + tableId;
    }

    @GetMapping("/called-items")
    public ResponseEntity<List<OrderItem>> getCalledItems(@RequestParam Integer tableId) {
        Order order = orderService.findLastedOpenOrderByTableId(tableId).orElse(null);
        List<OrderItem.ItemStatus> statuses = List.of(
                OrderItem.ItemStatus.NEW, OrderItem.ItemStatus.COOKING,
                OrderItem.ItemStatus.READY, OrderItem.ItemStatus.CANCELED);
        if (order != null) {
            return ResponseEntity.ok(orderService.getOrderItemsByOrderAndStatuses(order.getId(), statuses));
        } else {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/served-items")
    public ResponseEntity<List<OrderItem>> getServedItems(@RequestParam Integer tableId) {
        Order order = orderService.findLastedOpenOrderByTableId(tableId).orElse(null);
        List<OrderItem.ItemStatus> statuses = List.of(OrderItem.ItemStatus.SERVED);
        if (order != null) {
            return ResponseEntity.ok(orderService.getOrderItemsByOrderAndStatuses(order.getId(), statuses));
        } else {
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/cancel-item")
    public ResponseEntity<Void> cancelOrderItem(@RequestParam Integer id) {
        orderService.updateItemStatus(id, OrderItem.ItemStatus.CANCELED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/serve-item")
    public ResponseEntity<Void> serveOrderItem(@RequestParam Integer id) {
        orderService.updateItemStatus(id, OrderItem.ItemStatus.SERVED);
        return ResponseEntity.ok().build();
    }

    private List<MenuItem> getAvailableMenuItems() {
        return menuItemService.getAvailableItems();
    }
}
