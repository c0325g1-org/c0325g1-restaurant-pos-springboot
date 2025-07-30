package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFeedbackRepository extends JpaRepository<Feedback, String> {
    boolean existsById(String id);
}
