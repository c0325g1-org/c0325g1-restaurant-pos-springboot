package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.BookingUpdateRequest;
import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.entity.DiningTable;
import com.something.restaurantpos.entity.Voucher;
import com.something.restaurantpos.repository.IBookingRepository;
import com.something.restaurantpos.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private IBookingRepository bookingRepository;

    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public Page<Booking> search(String keyword, Booking.BookingStatus status, Pageable pageable) {
        return bookingRepository.filter(keyword, status, pageable);
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Booking findById(Integer id) {
        return bookingRepository.findById(id).orElse(null);
    }

    @Override
    public Booking findByIdOrThrow(Integer id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking có ID = " + id));
    }

    @Override
    public void deleteById(Integer id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Page<Booking> findTrash(Pageable pageable) {
        return bookingRepository.findByDeletedTrue(pageable);
    }

    @Override
    public void softDelete(Integer id) {
        Booking booking = findByIdOrThrow(id);
        booking.markDeleted();
        bookingRepository.save(booking);
    }

    @Override
    public void restore(Integer id) {
        Booking booking = findByIdOrThrow(id);
        booking.restore();
        bookingRepository.save(booking);
    }

    @Override
    public long countDeleted() {
        return bookingRepository.countByDeletedTrue();
    }

    @Override
    public List<Booking> findByDate(LocalDate localDate) {
        return bookingRepository.findByDate(localDate);
    }

    @Override
    public boolean isTableBookedBetween(DiningTable table, LocalDateTime start, LocalDateTime end, Integer excludeId) {
        return bookingRepository.isTableBookedBetween(table, start, end, excludeId);
    }

    @Override
    public void updateBookingInfo(BookingUpdateRequest req) {
        Booking booking = bookingRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getTable() != null) {
            throw new IllegalStateException("Không thể sửa booking đã được gán bàn");
        }

        booking.setPeople(req.getPeople());
        booking.setDateTime(req.getDateTime());
        booking.setNote(req.getNote());

        bookingRepository.save(booking);
    }
}
