package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.AccountActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface IAccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Integer> {
    
    Optional<AccountActivationToken> findByToken(String token);
    
    Optional<AccountActivationToken> findByTokenAndUsedFalse(String token);
    
    Optional<AccountActivationToken> findByEmployeeIdAndUsedFalse(Integer employeeId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM AccountActivationToken t WHERE t.employee.id = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Integer employeeId);
} 