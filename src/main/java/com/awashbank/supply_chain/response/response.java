package com.awashbank.supply_chain.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class response {
    private Integer statusCode;
    private String message;
    private LocalDateTime processingTime = LocalDateTime.now();
}
