package com.something.restaurantpos.validator;

import com.something.restaurantpos.dto.VoucherDTO;
import com.something.restaurantpos.validator.annotation.DateRangeValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<DateRangeValid, VoucherDTO> {

    @Override
    public boolean isValid(VoucherDTO dto, ConstraintValidatorContext context) {
        if (dto.getValidFrom() == null || dto.getValidTo() == null) {
            return true;
        }
        boolean valid = !dto.getValidTo().isBefore(dto.getValidFrom());

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày hết hạn phải sau hoặc bằng ngày bắt đầu")
                    .addPropertyNode("validTo")
                    .addConstraintViolation();
        }
        return valid;
    }
}

