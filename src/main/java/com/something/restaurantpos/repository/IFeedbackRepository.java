package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFeedbackRepository extends JpaRepository<Feedback, String> {
    boolean existsById(String id);

    @Query("SELECT f FROM Feedback f WHERE (f.customerName LIKE CONCAT('%', :name, '%'))")
    Page<Feedback> search(@Param("name") String name,
                          Pageable pageable);
    @Query("SELECT f FROM Feedback f WHERE f.rating = 5 ORDER BY f.createdAt DESC")
    List<Feedback> findTop5FiveStarFeedbacks(Pageable pageable);
}
