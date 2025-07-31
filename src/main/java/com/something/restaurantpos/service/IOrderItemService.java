package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.OrderItem;

import java.time.LocalDate;
import java.util.List;

public interface IOrderItemService {
    List<OrderItem> findAllByOrderId(Integer orderId);
    List<OrderItem> findAllCreatedAtOnDate(LocalDate createdAtDate);
}
