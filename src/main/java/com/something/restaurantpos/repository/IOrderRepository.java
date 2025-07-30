package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IOrderRepository extends JpaRepository<Order, Integer>{
    Order findByFeedbackToken(String feedbackToken);
    List<Order> findByStatusNot(Order.OrderStatus status);

    List<Order> findByTableIdAndStatusOrderByCreatedAtDesc(Integer tableId, Order.OrderStatus status);

    List<Order> findByTableIdOrderByCreatedAtDesc(Integer tableId);
}
