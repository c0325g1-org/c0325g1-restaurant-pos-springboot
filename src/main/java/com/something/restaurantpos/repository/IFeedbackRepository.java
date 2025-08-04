package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IFeedbackRepository extends JpaRepository<Feedback, String> {
    boolean existsById(String id);

    @Query("""
    SELECT f FROM Feedback f
    LEFT JOIN Order o ON f.id = o.feedbackToken
    WHERE f.deleted = false
      AND (f.customerName LIKE %:keyword% OR o.employee.name LIKE %:keyword%)
""")
    Page<Feedback> search(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(f) > 0 FROM Feedback f WHERE f.id = :id")
    boolean existsIncludingDeleted(@Param("id") String id);

    @Query("""
    SELECT f FROM Feedback f
    LEFT JOIN Order o ON f.id = o.feedbackToken
    WHERE f.deleted = true
      AND (f.customerName LIKE %:keyword% OR o.employee.name LIKE %:keyword%)
""")
    Page<Feedback> searchDeleted(@Param("keyword") String keyword, Pageable pageable);

    long countByDeleted(boolean deleted);

}
