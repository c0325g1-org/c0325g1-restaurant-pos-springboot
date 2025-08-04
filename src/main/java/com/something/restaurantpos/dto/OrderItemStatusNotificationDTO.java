package com.something.restaurantpos.dto;

import com.something.restaurantpos.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemStatusNotificationDTO extends NotificationDTO {
    private OrderItem orderItem;
}
