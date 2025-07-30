package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.Payment;

public interface IPaymentService extends IService<Payment>{
    void processPayment(Integer invoiceId, PaymentDto paymentDto);
    void save(PaymentDto paymentDto);
}
