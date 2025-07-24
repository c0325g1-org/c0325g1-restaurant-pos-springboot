package com.something.restaurantpos.mapper;
import com.something.restaurantpos.dto.VoucherDTO;
import com.something.restaurantpos.entity.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    VoucherDTO toDTO(Voucher voucher);
    Voucher toEntity(VoucherDTO voucherDTO);
}