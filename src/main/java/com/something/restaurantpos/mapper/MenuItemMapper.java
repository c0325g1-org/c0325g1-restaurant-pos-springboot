package com.something.restaurantpos.mapper;

import com.something.restaurantpos.dto.MenuItemDTO;
import com.something.restaurantpos.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    @Mapping(target = "imageUrl", ignore = true)
    MenuItemDTO toDto(MenuItem menuItem);

    @Mapping(target = "imageUrl", ignore = true)
    MenuItem toEntity(MenuItemDTO menuItemDTO);
}
