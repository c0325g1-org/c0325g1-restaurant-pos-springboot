package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMenuCategoryRepository extends JpaRepository<MenuCategory, Integer> {
}
