package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Invoice;
import com.something.restaurantpos.repository.IInvoiceRepository;
import com.something.restaurantpos.service.IInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService implements IInvoiceService{
    @Autowired
    private IInvoiceRepository invoiceRepository;

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice findById(String id) {
        return null;
    }

    @Override
    public Invoice findByIdOrThrow(String id) {
        return invoiceRepository.findById(Integer.valueOf(id)).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
    
    @Override
    public Invoice findByOrderId(Integer orderId) {
        return invoiceRepository.findByOrder_Id(orderId);
    }
}
