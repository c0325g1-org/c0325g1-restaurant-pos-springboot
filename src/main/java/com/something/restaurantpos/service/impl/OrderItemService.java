package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.repository.IOrderItemRepository;
import com.something.restaurantpos.service.IOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemService implements IOrderItemService {
    private final IOrderItemRepository orderItemRepository;
    @Override
    public List<OrderItem> findAllByOrderId(Integer orderId) {
        return orderItemRepository.findAllByOrder_Id(orderId);
    }

    @Override
    public List<OrderItem> findAllCreatedAtOnDateAndStatus(LocalDate createdAtDate, OrderItem.ItemStatus status) {
        return orderItemRepository.findAllCreatedAtOnDateAndStatus(createdAtDate, status);
    }

    @Override
    public Optional<OrderItem> findById(Integer id) {
        return orderItemRepository.findById(id);
    }
}
