package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.BookingDTO;
import com.something.restaurantpos.dto.BookingUpdateRequest;
import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.entity.Voucher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface IBookingService extends IService<Booking>{
    Page<Booking> search(String keyword, Booking.BookingStatus status, Pageable pageable);
    List<Booking> findByDate(LocalDate localDate);
    boolean isTableBookedBetween(DiningTable table, LocalDateTime start, LocalDateTime end, Integer excludeId);
    void updateBookingInfo(BookingUpdateRequest req);

    List<Booking> findUpcomingUnremindedBookings(LocalDateTime now, LocalDateTime next15);
}
