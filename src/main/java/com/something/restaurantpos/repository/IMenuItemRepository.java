package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByIsAvailableTrue();

    @Query("SELECT m FROM MenuItem m WHERE (m.category.id = :idCategory OR :idCategory = 0) AND (m.name LIKE CONCAT('%', :name, '%')) and (m.deleted=false)")
    Page<MenuItem> search(@Param("name") String name,
                          @Param("idCategory") Integer idCategory,
                          Pageable pageable);

    long countByDeletedTrue();

    Page<MenuItem> findByDeletedTrue(Pageable pageable);
}
