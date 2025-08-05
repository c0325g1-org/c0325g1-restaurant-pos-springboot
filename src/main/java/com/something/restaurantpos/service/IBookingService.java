package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.BookingDTO;
import com.something.restaurantpos.entity.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface IBookingService {
    void save(Booking booking);
}
