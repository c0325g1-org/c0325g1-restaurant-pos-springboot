package com.something.restaurantpos.controller;

import com.something.restaurantpos.dto.GroupedKitchenOrderDTO;
import com.something.restaurantpos.dto.KitchenOrderDTO;
import com.something.restaurantpos.dto.StatusViewHelper;
import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.entity.base.AuditMetadata;
import com.something.restaurantpos.service.IKitchenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {
    @Autowired
    private IKitchenService kitchenService;

    @GetMapping
    public String kitchen(
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Order> orderPage;

        if (filter.equals("ALL")) {
            orderPage = kitchenService.getActiveOrders(pageable);
        } else {
            OrderItem.ItemStatus status = OrderItem.ItemStatus.valueOf(filter);
            orderPage = kitchenService.getActiveOrdersByItemStatus(status, pageable);
        }


        List<KitchenOrderDTO> wrapped = orderPage.stream()
                .map(KitchenOrderDTO::new)
                .toList();



        List<OrderItem> allItems = wrapped.stream()
                .flatMap(dto -> dto.getOrder().getItems().stream())
                .sorted(Comparator.comparing(AuditMetadata::getCreatedAt))
                .toList();
        List<Order> rawOrders = orderPage.getContent();
        List<GroupedKitchenOrderDTO> groupedOrders = groupOrdersByTableAndTime(allItems);

        model.addAttribute("groupedOrders", groupedOrders);
        model.addAttribute("allItems", rawOrders.stream()
                .flatMap(o -> o.getItems().stream()).toList());


        model.addAttribute("orders", wrapped);
        model.addAttribute("allItems", allItems);
        model.addAttribute("currentFilter", filter);
        model.addAttribute("statusHelper", new StatusViewHelper());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("orderPairs", groupOrdersInPairs(wrapped));


        return "pages/kitchen/dashboard";
    }
    // KitchenController.java hoặc bạn có thể đưa vào service
    public List<List<KitchenOrderDTO>> groupOrdersInPairs(List<KitchenOrderDTO> orders) {
        List<List<KitchenOrderDTO>> result = new ArrayList<>();
        for (int i = 0; i < orders.size(); i += 2) {
            int end = Math.min(i + 2, orders.size());
            result.add(orders.subList(i, end));
        }
        return result;
    }
    private List<GroupedKitchenOrderDTO> groupOrdersByTableAndTime( List<OrderItem> allItems) {
        List<GroupedKitchenOrderDTO> groupedKitchenOrderDTOS = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();
        int currentIndex = 0;
        for (OrderItem item : allItems){
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






    @PostMapping("/item/{id}/status")
    @Transactional
    public String updateItemStatus(@PathVariable Integer id, @RequestParam("status") OrderItem.ItemStatus status) {
        kitchenService.updateItemStatus(id, status);
        return "redirect:/kitchen";
    }

    @PostMapping("/order/{id}/delete")
    public String softDeleteOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        kitchenService.softDeleteOrder(id);
        redirectAttributes.addFlashAttribute("success", "Đơn #" + id + " đã được ẩn khỏi danh sách.");
        return "redirect:/kitchen";
    }



}
