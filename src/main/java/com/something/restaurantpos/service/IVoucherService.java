package com.something.restaurantpos.service;


import com.something.restaurantpos.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IVoucherService extends IService<Voucher> {
    Page<Voucher> search(String keyword, Boolean status, Integer percent, Pageable pageable);
    List<Voucher> findAllValid();
    Page<Voucher> findByValidToAfter(LocalDateTime now, Pageable pageable);}
