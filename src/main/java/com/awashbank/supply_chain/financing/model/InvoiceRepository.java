package com.awashbank.supply_chain.financing.model;

import com.awashbank.supply_chain.user.model.UserDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND (i.seller.id = :userid OR i.buyer.id = :userid)")
    List<Invoice> findByStatusAndUser(@Param("status") String status, @Param("user") Long userid);
}
