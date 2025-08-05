package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Employee;
import com.something.restaurantpos.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findAllByRole_UserRole(Role.UserRole role);
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmail(String email);
    
    // Thêm các method cho admin
    long countByRoleId(Integer roleId);
    long countByEnableTrue();
    
    @Query("SELECT e FROM Employee e JOIN FETCH e.role")
    List<Employee> findAllWithRole();
}