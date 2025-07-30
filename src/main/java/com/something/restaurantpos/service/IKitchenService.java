package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IKitchenService {
    Page<Order> getActiveOrders( Pageable pageable);
    void updateItemStatus(Integer id, OrderItem.ItemStatus newStatus);
    Page<Order> getActiveOrdersByItemStatus(OrderItem.ItemStatus status, Pageable pageable);
    void softDeleteOrder(Integer id);

}
