package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.MenuCategory;

import java.util.List;

public interface IMenuCategoryService {
    List<MenuCategory> findAll();
}
