package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.entity.Payment;
import com.something.restaurantpos.repository.IIPaymentRepository;
import com.something.restaurantpos.repository.IInvoiceRepository;
import com.something.restaurantpos.service.IPaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class PaymentServiceImpl implements IPaymentService {
    @Autowired
    private IInvoiceRepository  invoiceRepository;
    @Autowired
    private IIPaymentRepository paymentRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public void processPayment(Integer invoiceId, PaymentDto paymentDto) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với id: " + invoiceId));
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentTime(LocalDateTime.now());
        try {
            Payment.Method method = Payment.Method.valueOf(paymentDto.getMethod().toUpperCase());
            payment.setMethod(method);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Phương thức thanh toán không hợp lệ: " + paymentDto.getMethod());
        }
        paymentRepository.save(payment);
    }
    @Override
    public void save(PaymentDto paymentDto) {
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        Invoice invoice = invoiceRepository.findById(paymentDto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + paymentDto.getInvoiceId()));
        payment.setInvoice(invoice);
        paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAll() {
        return List.of();
    }

    @Override
    public Payment findById(Integer id) {
        return null;
    }

    @Override
    public Payment findByIdOrThrow(Integer id) {
        return null;
    }

    @Override
    public void save(Payment payment) {

    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public Page<Payment> findTrash(Pageable pageable) {
        return null;
    }

    @Override
    public void softDelete(Integer id) {

    }

    @Override
    public void restore(Integer id) {

    }

    @Override
    public long countDeleted() {
        return 0;
    }

}
