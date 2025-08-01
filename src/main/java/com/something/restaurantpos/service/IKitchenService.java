package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.OrderItem;

public interface IKitchenService {
    void updateItemStatus(Integer id, OrderItem.ItemStatus newStatus);
    void softDeleteOrderItem(Integer id);
//    Page<Order> getActiveOrdersByDate(LocalDate date, Pageable pageable);
//    Page<Order> getActiveOrdersByItemStatusAndDate(OrderItem.ItemStatus status, LocalDate date, Pageable pageable);


}
