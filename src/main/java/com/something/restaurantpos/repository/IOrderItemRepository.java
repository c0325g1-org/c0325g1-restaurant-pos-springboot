package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    AND DATE(o.createdAt) = :date
""")
    Page<Order> findActiveOrdersWithItemsByDate(@Param("date") LocalDate date, Pageable pageable);
    @Query("""
    SELECT DISTINCT o FROM Order o
    JOIN o.items i
    WHERE i.status = :status
    AND o.status != 'CLOSED'
    AND DATE(o.createdAt) = :date
""")
    Page<Order> findOrdersByItemStatusAndDate(@Param("status") OrderItem.ItemStatus status,@Param("date") LocalDate date, Pageable pageable);

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrder_Table_IdAndStatusIn(Integer tableId, List<OrderItem.ItemStatus> statuses);

    boolean existsOrderItemByOrderIdAndOrder_Table_Id(Integer orderId, Integer tableId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);
}
