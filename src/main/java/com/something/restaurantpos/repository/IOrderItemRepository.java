package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN o.items i
    WHERE i.status IN (
        com.something.restaurantpos.entity.OrderItem.ItemStatus.NEW,
        com.something.restaurantpos.entity.OrderItem.ItemStatus.COOKING,
        com.something.restaurantpos.entity.OrderItem.ItemStatus.READY
    )
    AND o.status != 'CLOSED'
""")
    Page<Order> findActiveOrdersWithItems(Pageable pageable);
    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN o.items i
    WHERE i.status = :status
    AND o.status != 'CLOSED'
""")
    Page<Order> findOrdersByItemStatus(@Param("status") OrderItem.ItemStatus status, Pageable pageable);




}
