package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IMenuItemRepository extends JpaRepository<MenuItem,Integer> {
    Optional<MenuItem> findByName(String name);
}
