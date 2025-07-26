package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeeRepository extends JpaRepository<Employee, Integer> { }
