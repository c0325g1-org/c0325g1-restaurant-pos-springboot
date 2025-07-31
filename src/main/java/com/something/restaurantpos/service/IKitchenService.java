package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IKitchenService {
    void updateItemStatus(Integer id, OrderItem.ItemStatus newStatus);
    void softDeleteOrder(Integer id);
//    Page<Order> getActiveOrdersByDate(LocalDate date, Pageable pageable);
//    Page<Order> getActiveOrdersByItemStatusAndDate(OrderItem.ItemStatus status, LocalDate date, Pageable pageable);


}
