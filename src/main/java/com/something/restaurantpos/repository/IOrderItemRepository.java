package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Order;
import com.something.restaurantpos.entity.OrderItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface IOrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrder_Table_IdAndStatusIn(Integer tableId, List<OrderItem.ItemStatus> statuses);

    boolean existsOrderItemByOrderIdAndOrder_Table_Id(Integer orderId, Integer tableId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);
}
