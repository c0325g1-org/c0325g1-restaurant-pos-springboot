package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.repository.IOrderItemRepository;
import com.something.restaurantpos.repository.IOrderRepository;
import com.something.restaurantpos.service.IKitchenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class KitChenService implements IKitchenService {

    @Autowired
    private IOrderItemRepository orderItemRepository;

    @Autowired
    private IOrderRepository orderRepository;

    @Override
    public Page<Order> getActiveOrders(Pageable pageable) {
        return orderItemRepository.findActiveOrdersWithItems(pageable);
    }
    @Override
    public Page<Order> getActiveOrdersByItemStatus(OrderItem.ItemStatus status, Pageable pageable) {
        return orderItemRepository.findOrdersByItemStatus(status, pageable);
    }


    @Override
    public void updateItemStatus(Integer id, OrderItem.ItemStatus newStatus) {
        OrderItem item = orderItemRepository.findById(id).orElseThrow();
        item.setStatus(newStatus);
        orderItemRepository.save(item);

        Order order = item.getOrder();

        boolean allServed = order.getItems().stream()
                .allMatch(i -> i.getStatus() == OrderItem.ItemStatus.SERVED);

        if (allServed) {
            order.setStatus(Order.OrderStatus.CLOSED);
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void softDeleteOrder(Integer id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.markDeleted();
        orderRepository.save(order);
    }





}

