package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.repository.IOrderRepository;
import com.something.restaurantpos.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private IOrderRepository orderRepository;
    
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order findByIdOrThrow(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public void save(Order order) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Page<Order> findTrash(Pageable pageable) {
        return null;
    }

    @Override
    public void softDelete(Integer id) {

    }

    @Override
    public void restore(Integer id) {

    }

    @Override
    public long countDeleted() {
        return 0;
    }
    
}
