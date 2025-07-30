package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import lombok.Getter;
@Getter
public class KitchenOrderDTO {
    private final Order order;
    private final boolean late;

    public KitchenOrderDTO(Order order) {
        this.order = order;
        this.late = java.time.Duration.between(order.getCreatedAt(), java.time.LocalDateTime.now()).toMinutes() > 15;
    }

    public boolean hasStatus(OrderItem.ItemStatus status) {
        return order.getItems().stream().anyMatch(i -> i.getStatus() == status);
    }

    public boolean isAllReady() {
        return order.getItems().stream().allMatch(i -> i.getStatus() == OrderItem.ItemStatus.READY);
    }

    public boolean isAllServed() {
        return order.getItems().stream().allMatch(i -> i.getStatus() == OrderItem.ItemStatus.SERVED);
    }


}


