package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Invoice;

import java.util.List;

public interface IInvoiceService {
    Invoice findByOrderId(Integer orderId);
    List<Invoice> findAll();
    Invoice findById(String id);
    Invoice findByIdOrThrow(String id);
}
