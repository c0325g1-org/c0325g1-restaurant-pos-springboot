package com.something.restaurantpos.repository;

import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IIPaymentRepository extends JpaRepository<Payment, Integer> {
}
