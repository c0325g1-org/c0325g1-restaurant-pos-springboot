package com.something.restaurantpos.service;

import com.something.restaurantpos.entity.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMenuCategoryService extends IService<MenuCategory>{
    Page<MenuCategory> findAllCategory(Pageable pageable);
    List<MenuCategory> findAll();
}
