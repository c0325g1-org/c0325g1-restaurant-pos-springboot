package com.something.restaurantpos.config;

import com.something.restaurantpos.dto.InvoiceDto;
import com.something.restaurantpos.entity.Invoice;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(Invoice.class, InvoiceDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getDiningTable().getName(), InvoiceDto::setTableName);
            mapper.map(src -> src.getDiningTable().getName(), InvoiceDto::setEmployeeName);
        });
        return modelMapper;

    }
}
