package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@AllArgsConstructor
public class GroupedKitchenOrderDTO {
    private final String tableName;
    private final LocalDateTime createdAt;
    private final List<OrderItem> orderItems;

    public boolean hasCanceledItems() {
        return orderItems.stream()
                .anyMatch(i -> i.getStatus() == OrderItem.ItemStatus.CANCELED);
    }
    public Integer getOrderId() {
        return orderItems.get(0).getOrder().getId(); // Vì tất cả item trong group đều cùng 1 order
    }

}
