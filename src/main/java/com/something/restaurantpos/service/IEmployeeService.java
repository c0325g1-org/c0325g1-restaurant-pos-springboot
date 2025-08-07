package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.Employee;

import java.util.Optional;

public interface IEmployeeService {
    Optional<Employee> findByUsername(String username);
    boolean updatePassword(String username, String currentPassword, String newPassword);
}
