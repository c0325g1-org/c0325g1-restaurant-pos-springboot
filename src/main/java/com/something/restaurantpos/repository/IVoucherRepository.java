package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IVoucherRepository extends JpaRepository<Voucher, Integer> {
    Page<Voucher> findByDeletedFalse(Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE v.deleted = false " +
            "AND (:keyword IS NULL OR LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR v.isActive = :status) " +
            "AND (:percent IS NULL OR v.discountPercent = :percent)")
    Page<Voucher> filter(@Param("keyword") String keyword,
                                 @Param("status") Boolean status,
                                 @Param("percent") Integer percent,
                                 Pageable pageable);

    Page<Voucher> findByDeletedTrue(Pageable pageable);

    long countByDeletedTrue();
}
