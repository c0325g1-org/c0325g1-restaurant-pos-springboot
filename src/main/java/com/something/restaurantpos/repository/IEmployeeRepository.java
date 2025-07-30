package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findAllByRole_UserRole(Role.UserRole role);
}
