package com.awashbank.supply_chain.financing.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceAprove {
    private Long invoiceId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal approved_amount;
    private String approved_percent;
    private String dueDate;
    private INVOICESTATUS status;

    public enum INVOICESTATUS{
        PENDING, APPROVED, PAID, REPAID,REJECTED
    }
}
