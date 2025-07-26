package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrder_Table_IdAndStatusIn(Integer tableId, List<OrderItem.ItemStatus> statuses);
}
