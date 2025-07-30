package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDiningTableRepository extends JpaRepository<DiningTable, Integer> {
    List<DiningTable> findByStatusIn(List<DiningTable.TableStatus> statusList);

    boolean existsOpenOrderById(Integer id);
}
