package com.something.restaurantpos.dto;

import com.something.restaurantpos.dto.base.BaseDTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDTO extends BaseDTO {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String customerName;

    @NotBlank(message = "sdt không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có đúng 9 chữ số sau đó")
    private String customerPhone;

    @NotNull(message = "Vui lòng chọn số sao đánh giá")
    @Min(value = 1, message = "Đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa là 5 sao")
    private Integer rating;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    private String content;

    private String imagePath; 

    private String uuid; 
}
