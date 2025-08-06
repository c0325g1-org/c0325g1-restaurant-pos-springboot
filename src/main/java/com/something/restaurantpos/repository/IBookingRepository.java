package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBookingRepository extends JpaRepository<Booking,Integer> {
    List<Booking> findAllByEmailIsNotNull();

}
