package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.OrderItem;
import com.something.restaurantpos.repository.IOrderItemRepository;
import com.something.restaurantpos.service.IOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService implements IOrderItemService {
    private final IOrderItemRepository orderItemRepository;
    @Override
    public List<OrderItem> findAllByOrderId(Integer orderId) {
        return orderItemRepository.findAllByOrder_Id(orderId);
    }

    @Override
    public List<OrderItem> findAllCreatedAtOnDate(LocalDate createdAtDate) {
        return orderItemRepository.findAllCreatedAtOnDate(createdAtDate);
    }
}
