package com.something.restaurantpos.dto;

import com.something.restaurantpos.dto.base.BaseDTO;
import com.something.restaurantpos.entity.MenuCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDTO extends BaseDTO {
    private Integer id;

    @NotBlank(message = "tên không được để trống")
    @Pattern(regexp = "^[A-Za-zÀ-ỹà-ỹ0-9\\s]{2,100}$", message = "Chỉ nhập chữ và khoảng trắng, độ dài 2-100 ký tự")
    private String name;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Ảnh không được để trống")
    private MultipartFile imageUrl;
    private String imageUrlString;

    @NotNull(message = "Loại không được để trống")
    private MenuCategory category;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isAvailable = true;
    private String croppedImage;
}
