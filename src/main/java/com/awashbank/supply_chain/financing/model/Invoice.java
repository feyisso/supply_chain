package com.awashbank.supply_chain.financing.model;

import com.awashbank.supply_chain.user.model.UserDetailModel;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private BigDecimal approved_amount;
    private String approved_percent;
    private LocalDate dueDate;
    private String status; // PENDING, APPROVED, PAID, REPAID
    @ManyToOne
    private UserDetailModel seller;
    @ManyToOne
    private UserDetailModel buyer;
    @ManyToOne
    private UserDetailModel financier;
    @ManyToOne
    private UserDetailModel approved_by;
}
