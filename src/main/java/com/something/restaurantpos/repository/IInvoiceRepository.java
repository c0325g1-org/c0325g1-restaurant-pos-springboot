package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, Integer> {
    Invoice findByOrder_Id(Integer orderId);

    @Query("SELECT i FROM Invoice i " +
            "LEFT JOIN Payment p ON p.invoice = i " +
            "WHERE i.order.status = com.something.restaurantpos.entity.Order.OrderStatus.CLOSED " +
            "AND p.id IS NULL")
    List<Invoice> findReadyToCheckout();

    @Query("UPDATE Invoice i SET i.paid = true WHERE i.id = :id")
    void checkoutInvoice(@Param("id") Integer id);

    @Query("DELETE FROM OrderItem o WHERE o.id = :invoiceId")
    void deleteByInvoiceId(@Param("invoiceId") Integer invoiceId);

    @Query("SELECT COUNT(i) FROM Invoice i " +
            "WHERE i.paid = true AND i.deleted = false " +
            "AND DATE(i.createdAt) = CURRENT_DATE")
    long countPaidInvoicesToday();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
            "WHERE i.paid = true AND i.deleted = false " +
            "AND DATE(i.createdAt) = CURRENT_DATE")
    BigDecimal sumRevenueToday();

    @Query("SELECT DATE(i.createdAt) AS date, SUM(i.totalAmount) AS total " +
            "FROM Invoice i " +
            "WHERE i.paid = true AND i.deleted = false " +
            "AND i.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(i.createdAt) " +
            "ORDER BY DATE(i.createdAt)")
    List<Object[]> getRevenueBetweenDates(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

}
