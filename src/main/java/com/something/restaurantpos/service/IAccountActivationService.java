package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Employee;

public interface IAccountActivationService {

    void createActivationTokenAndSendEmail(Employee employee);

    boolean activateAccount(String token);

    void resendActivationEmail(Integer employeeId);

    boolean changePasswordAfterActivation(String token, String newPassword);

    void deleteTokensByEmployeeId(Integer employeeId);
} 