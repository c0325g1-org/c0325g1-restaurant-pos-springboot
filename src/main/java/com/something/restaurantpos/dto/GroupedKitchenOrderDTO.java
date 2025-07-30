package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.Order;
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
}
