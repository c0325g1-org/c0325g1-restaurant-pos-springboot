package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookingRepository extends JpaRepository<Booking,Integer> {


}
