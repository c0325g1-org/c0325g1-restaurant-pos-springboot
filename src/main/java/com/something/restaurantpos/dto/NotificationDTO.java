package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Order order;
    private List<OrderItem> orderItems;
    private Integer tableId;
    private String message;
    private boolean isNewOrder;
}
