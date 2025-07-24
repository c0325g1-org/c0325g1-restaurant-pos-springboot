package com.something.restaurantpos.exception;

public class VoucherNotFoundException extends RuntimeException {
    public VoucherNotFoundException(Integer id) {
        super("Không tìm thấy voucher có ID: " + id);
    }
}
