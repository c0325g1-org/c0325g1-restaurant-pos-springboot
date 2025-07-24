package com.something.restaurantpos.service.impl;

import com.something.restaurantpos.entity.Voucher;
import com.something.restaurantpos.repository.IVoucherRepository;
import com.something.restaurantpos.service.IVoucherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService implements IVoucherService {

    private final IVoucherRepository voucherRepository;

    @Override
    public List<Voucher> findAll() {
        return voucherRepository.findByDeletedFalse(Pageable.unpaged()).getContent();
    }

    @Override
    public Voucher findById(Integer id) {
        return voucherRepository.findById(id).orElse(null);
    }

    @Override
    public Voucher findByIdOrThrow(Integer id) {
        return voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher có ID = " + id));
    }

    @Override
    @Transactional
    public void save(Voucher voucher) {
        voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        voucherRepository.deleteById(id);
    }

    @Override
    public Page<Voucher> findTrash(Pageable pageable) {
        return voucherRepository.findByDeletedTrue(pageable);
    }

    @Override
    @Transactional
    public void softDelete(Integer id) {
        Voucher voucher = findByIdOrThrow(id);
        voucher.markDeleted();
        voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void restore(Integer id) {
        Voucher voucher = findByIdOrThrow(id);
        voucher.restore();
        voucherRepository.save(voucher);
    }

    @Override
    public Page<Voucher> search(String keyword, Boolean status, Integer percent, Pageable pageable) {
        return voucherRepository.filter(keyword, status, percent, pageable);
    }

    @Override
    public long countDeleted() {
        return voucherRepository.countByDeletedTrue();
    }
}
