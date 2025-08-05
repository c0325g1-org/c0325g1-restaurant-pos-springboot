package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Booking;
import com.something.restaurantpos.repository.IBookingRepository;
import com.something.restaurantpos.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService implements IBookingService {
    @Autowired
    private IBookingRepository bookingRepository;
    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }
}
