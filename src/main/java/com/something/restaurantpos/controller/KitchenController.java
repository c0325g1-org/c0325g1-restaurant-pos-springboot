package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.GroupedKitchenOrderDTO;
import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.service.IKitchenService;
import com.something.restaurantpos.service.IOrderItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {
    @Autowired
    private IKitchenService kitchenService;
    @Autowired
    private IOrderItemService orderItemService;

    @GetMapping("/dashboard")
    public String kitchen(
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        if(date == null){
            date = LocalDate.now();
        }
        OrderItem.ItemStatus status = null;
        try {
            status = OrderItem.ItemStatus.valueOf(filter);
        } catch (IllegalArgumentException ignored) {
        }
        List<OrderItem> allItems = orderItemService.findAllCreatedAtOnDateAndStatus(date, status);
        List<GroupedKitchenOrderDTO> groupedOrders = groupOrdersByTableAndTime(allItems);
        List<GroupedKitchenOrderDTO> visibleGroups = groupedOrders.stream()
                .filter(this::shouldDisplayGroup)
                .toList(); // hoặc .collect(Collectors.toList()) nếu dùng Java < 16

        model.addAttribute("groupedOrders", visibleGroups);

        model.addAttribute("allItems", allItems);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("selectedDate", date.toString());
        model.addAttribute("currentDate", date);

        return "pages/kitchen/dashboard";
    }
    private boolean shouldDisplayGroup(GroupedKitchenOrderDTO group) {
        return group.getOrderItems().stream()
                .anyMatch(item -> !item.isDeleted() && item.getStatus() != OrderItem.ItemStatus.SERVED);
    }

    private List<GroupedKitchenOrderDTO> groupOrdersByTableAndTime( List<OrderItem> allItems) {
        List<GroupedKitchenOrderDTO> groupedKitchenOrderDTOS = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();
        int currentIndex = 0;
        for (OrderItem item : allItems){
            if (item.isDeleted()) {
                continue;
            }
            if (groupedKitchenOrderDTOS.isEmpty()){
                orderItems.add(item);
                groupedKitchenOrderDTOS.add(new GroupedKitchenOrderDTO(
                        item.getOrder().getTable().getName(),
                        item.getCreatedAt(),
                        orderItems
                ));
            } else {
                if (item.getOrder().getTable().getName().equals(groupedKitchenOrderDTOS.get(currentIndex).getTableName())) {
                    groupedKitchenOrderDTOS.get(currentIndex).getOrderItems().add(item);
                } else {
                    orderItems = new ArrayList<>();
                    orderItems.add(item);
                    groupedKitchenOrderDTOS.add(new GroupedKitchenOrderDTO(
                            item.getOrder().getTable().getName(),
                            item.getCreatedAt(),
                            orderItems
                    ));
                    currentIndex++;
                }
            }
        }
        return groupedKitchenOrderDTOS;
    }





    @PostMapping("/dashboard/item/{id}/status")
    @Transactional
    public String updateItemStatusWithUndo(
            @PathVariable Integer id,
            @RequestParam("status") OrderItem.ItemStatus status,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("filter") String filter,
            RedirectAttributes redirectAttributes
    ) {
        OrderItem item = orderItemService.findById(id).orElseThrow();
        OrderItem.ItemStatus oldStatus = item.getStatus();

        kitchenService.updateItemStatus(id, status);

        // Gửi flash message với trạng thái cũ để hiển thị Undo
        redirectAttributes.addFlashAttribute("undoItemId", id);
        redirectAttributes.addFlashAttribute("undoOldStatus", oldStatus);
        redirectAttributes.addFlashAttribute("undoFilter", filter);
        redirectAttributes.addFlashAttribute("undoDate", date.toString());

        return "redirect:/kitchen/dashboard?filter=" + filter + "&date=" + date;
    }
    @PostMapping("/dashboard/item/{id}/undo")
    @Transactional
    public String undoItemStatus(
            @PathVariable Integer id,
            @RequestParam("status") OrderItem.ItemStatus status,
            @RequestParam("filter") String filter,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes
    ) {
        kitchenService.updateItemStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Đã hoàn tác trạng thái món.");
        return "redirect:/kitchen/dashboard?filter=" + filter + "&date=" + date;
    }

    @PostMapping("/hide/orderItem")
    @Transactional
    public String hideOrderFromKitchen(
            @RequestParam("orderId") Integer id,
            @RequestParam("filter") String filter,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            RedirectAttributes redirectAttributes
    ) {
        kitchenService.softDeleteOrderItem(id);
        redirectAttributes.addFlashAttribute("success", "Đã ẩn món khỏi màn hình bếp.");
        return "redirect:/kitchen/dashboard?filter=" + filter + "&date=" + date;
    }








}
