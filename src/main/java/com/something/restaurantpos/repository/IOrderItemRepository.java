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
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByOrder(Order order);

    List<OrderItem> findAllByOrder_Table_IdAndStatusIn(Integer tableId, List<OrderItem.ItemStatus> statuses);

    boolean existsOrderItemByOrderIdAndOrder_Table_Id(Integer orderId, Integer tableId);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);

    List<OrderItem> findAllByOrder_Id(Integer orderId);

    @Query(value = "SELECT oi FROM OrderItem oi WHERE (DATE(oi.createdAt) = :targetDate or :targetDate is null) and (oi.status = :status or :status is null)")
    List<OrderItem> findAllCreatedAtOnDateAndStatus(@Param("targetDate") LocalDate date, @Param("status") OrderItem.ItemStatus status);
}
