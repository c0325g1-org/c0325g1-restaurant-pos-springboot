package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.dto.PaymentDto;
import com.something.restaurantpos.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IInvoiceService extends IService<Invoice> {
    List<Invoice> findReadyToCheckout();
    InvoiceDto findDtoById(Integer id);
    List<InvoiceDto> findAllDto();
    void processPayment(PaymentDto paymentDto);
    Page<InvoiceDto> findAllDtoPage(Pageable pageable);
    void updateInvoiceWithItems(Integer id, InvoiceDto invoiceDto);
}
