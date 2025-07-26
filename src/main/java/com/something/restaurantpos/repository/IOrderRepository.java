package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByTableIdAndStatus(Integer tableId, Order.OrderStatus status);
}
