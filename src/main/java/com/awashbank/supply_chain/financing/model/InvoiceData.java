package com.awashbank.supply_chain.financing.model;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Data
public class InvoiceData {
    private Long buyerId;
    private Long sellerId;
    private BigDecimal amount;
}
