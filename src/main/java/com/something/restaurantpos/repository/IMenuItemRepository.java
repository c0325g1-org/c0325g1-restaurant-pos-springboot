package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.MenuItem;
import com.something.restaurantpos.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

@Repository
public interface IMenuItemRepository extends JpaRepository<MenuItem,Integer> {
    Optional<MenuItem> findByName(String name);

    List<MenuItem> findByIsAvailableTrue();

    @Query("SELECT m FROM MenuItem m WHERE (m.category.id = :idCategory OR :idCategory = 0) AND (m.name LIKE CONCAT('%', :name, '%')) and (m.deleted=false)")
    Page<MenuItem> search(@Param("name") String name,
                          @Param("idCategory") Integer idCategory,
                          Pageable pageable);

    long countByDeletedTrue();

    Page<MenuItem> findByDeletedTrue(Pageable pageable);
    @Query("SELECT mi FROM OrderItem oi JOIN oi.menuItem mi  GROUP BY mi ORDER BY SUM(oi.quantity) DESC limit 6")
    List<MenuItem> findMenuItemOrderByTotalQuantityDesc();

    @Query("SELECT COUNT(m) FROM MenuItem m WHERE m.deleted = false AND m.isAvailable = true")
    long countSellingItems();
}
