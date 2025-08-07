package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.AccountActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Integer> {
    
    Optional<AccountActivationToken> findByTokenAndUsedFalse(String token);
    
    Optional<AccountActivationToken> findByEmployeeIdAndUsedFalse(Integer employeeId);
    
    void deleteByEmployeeId(Integer employeeId);
} 