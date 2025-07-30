package com.something.restaurantpos.repository;

import com.something.restaurantpos.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, Integer> {
    Invoice findByOrder_Id(Integer orderId);
}
