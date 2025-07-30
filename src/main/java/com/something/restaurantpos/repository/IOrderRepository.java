package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<Order,Integer> {
    List<Order> findByStatusNot(Order.OrderStatus status);
}
