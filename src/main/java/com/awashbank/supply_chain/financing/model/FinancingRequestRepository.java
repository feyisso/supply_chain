package com.awashbank.supply_chain.financing.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinancingRequestRepository extends JpaRepository<FinancingRequest, Long> {
    @Query("SELECT i FROM FinancingRequest i WHERE i.status = :status AND (i.invoice.seller.id = :userid OR i.invoice.buyer.id = :userid)")
    List<FinancingRequest> findByStatusAndUser(@Param("status") String status, @Param("user") Long userid);
}
