package com.something.restaurantpos.dto;

import com.something.restaurantpos.dto.base.BaseDTO;
import com.something.restaurantpos.validator.annotation.DateRangeValid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DateRangeValid
public class VoucherDTO extends BaseDTO {

    private Integer id;

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 50, message = "Mã voucher tối đa 50 ký tự")
    private String code;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá phải từ 1%")
    @Max(value = 100, message = "Phần trăm giảm giá tối đa 100%")
    private Integer discountPercent;

    @NotNull(message = "Ngày bắt đầu hiệu lực không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime validFrom;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime validTo;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive = true;
    @Size(max = 255, message = "Đường dẫn ảnh tối đa 255 ký tự")
    private String image;
    private MultipartFile imageFile;
}
