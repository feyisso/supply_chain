package com.awashbank.supply_chain.financing.model;

import com.awashbank.supply_chain.user.model.UserDetailModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FinancingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String financingType; // INVOICE_DISCOUNTING, INVOICE_FACTORING, REVERSE_FACTORING, DISTRIBUTED_FINANCING
    private String status; // PENDING, APPROVED, PAID, REPAID
    private String region_code;
    private String branch_code;
    @OneToOne
    private Invoice invoice;
    @ManyToOne
    private UserDetailModel initiator;
    @ManyToOne
    private UserDetailModel approver;
}