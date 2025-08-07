package com.something.restaurantpos.dto;

import lombok.Data;

@Data
public class ChangeProfilePasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
} 