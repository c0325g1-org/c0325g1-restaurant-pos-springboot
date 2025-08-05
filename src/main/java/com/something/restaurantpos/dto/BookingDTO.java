package com.something.restaurantpos.dto;

import com.something.restaurantpos.dto.base.BaseDTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class BookingDTO extends BaseDTO {
    @NotBlank(message = "Tên không được để trống")
    @Pattern(regexp = "^[A-Za-zÀ-ỹà-ỹ0-9\\s]{2,100}$", message = "Chỉ nhập chữ, số và khoảng trắng, độ dài 2-100 ký tự")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng 0 hoặc +84 và có đúng 9 chữ số sau đó")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Nhập đúng định dạng email, ví dụ: example@gmail.com")
    private String email;

    @NotNull(message = "Ngày giờ không được để trống")
    @FutureOrPresent(message = "Ngày giờ phải là hiện tại hoặc tương lai")
    private LocalDateTime dateTime;

    @NotNull(message = "Số lượng khách không được để trống")
    @Min(value = 1, message = "Ít nhất 1 khách")
    @Max(value = 100, message = "Tối đa chỉ 100 khách")
    private Integer people;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String note;
}
