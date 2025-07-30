package com.something.restaurantpos.service;

import com.something.restaurantpos.dto.ForgotPasswordDTO;
import com.something.restaurantpos.dto.ResetPasswordDTO;
import org.springframework.transaction.annotation.Transactional;

public interface IPasswordResetService {
    boolean sendPasswordResetEmail(ForgotPasswordDTO forgotPasswordDTO);
    boolean verifyToken(String token);
    boolean resetPassword(ResetPasswordDTO resetPasswordDTO);
} 