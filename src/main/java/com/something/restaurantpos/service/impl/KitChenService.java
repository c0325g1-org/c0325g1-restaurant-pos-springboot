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

import java.time.LocalDate;
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
    public void updateItemStatus(Integer id, OrderItem.ItemStatus newStatus) {
        OrderItem item = orderItemRepository.findById(id).orElseThrow();
        item.setStatus(newStatus);
        orderItemRepository.save(item);

        Order order = item.getOrder();

        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(order.getId());
        boolean allServed = orderItems.stream()
                .allMatch(i -> i.getStatus() == OrderItem.ItemStatus.SERVED);

        if (allServed) {
            order.setStatus(Order.OrderStatus.CLOSED);
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void softDeleteOrderItem(Integer id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElseThrow();
        orderItem.markDeleted();
        orderItemRepository.save(orderItem);
    }

//    @Override
//    public Page<Order> getActiveOrdersByDate(LocalDate date, Pageable pageable) {
//        return orderItemRepository.findActiveOrdersWithItemsByDate(date,pageable);
//    }
//
//    @Override
//    public Page<Order> getActiveOrdersByItemStatusAndDate(OrderItem.ItemStatus status, LocalDate date, Pageable pageable) {
//        return orderItemRepository.findOrdersByItemStatusAndDate(status,date,pageable);
//    }


}

