package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.OrderItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IOrderItemService {
    List<OrderItem> findAllByOrderId(Integer orderId);
    List<OrderItem> findAllCreatedAtOnDateAndStatus(LocalDate createdAtDate, OrderItem.ItemStatus status);

    Optional<OrderItem> findById(Integer id);
}
