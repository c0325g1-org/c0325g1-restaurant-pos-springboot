package com.something.restaurantpos.mapper;

import com.something.restaurantpos.dto.MenuItemDTO;
import com.something.restaurantpos.entity.MenuItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    MenuItemDTO toDto(MenuItem menuItem);
    MenuItem toEntity(MenuItemDTO menuItemDTO);
}
