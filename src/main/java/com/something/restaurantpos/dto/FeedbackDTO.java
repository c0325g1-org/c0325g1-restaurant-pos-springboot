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

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Số điện thoại không hợp lệ")
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
