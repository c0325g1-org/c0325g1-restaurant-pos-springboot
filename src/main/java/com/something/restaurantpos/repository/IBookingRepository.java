package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IBookingRepository extends JpaRepository<Booking,Integer> {

    Page<Booking> findByDeletedFalse(Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.deleted = false " +
            "AND ((:keyword IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "OR (:keyword IS NULL OR LOWER(b.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "OR (:keyword IS NULL OR LOWER(b.email) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND (:status IS NULL OR b.status = :status)")
    Page<Booking> filter(@Param("keyword") String keyword,
                         @Param("status") Booking.BookingStatus status,
                         Pageable pageable);

    Page<Booking> findByDeletedTrue(Pageable pageable);

    long countByDeletedTrue();

    @Query("SELECT b FROM Booking b WHERE b.deleted = false AND DATE(b.dateTime) = :date")
    List<Booking> findByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.table = :table " +
            "AND b.dateTime BETWEEN :start AND :end " +
            "AND b.id <> :excludeId " +
            "AND b.status <> 'CANCELLED'")
    boolean isTableBookedBetween(
            @Param("table") DiningTable table,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") Integer excludeId
    );
}
