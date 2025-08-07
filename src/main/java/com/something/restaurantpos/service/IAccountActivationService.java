package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Employee;

public interface IAccountActivationService {
    
    /**
     * Tạo token kích hoạt và gửi email cho nhân viên mới
     */
    void createActivationTokenAndSendEmail(Employee employee);
    
    /**
     * Kích hoạt tài khoản bằng token
     */
    boolean activateAccount(String token);
    
    /**
     * Gửi lại email kích hoạt cho nhân viên
     */
    void resendActivationEmail(Integer employeeId);
} 