package com.realtimefraud.fraud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String transactionId;
    private String customerId;
    private String merchantId;
    private Double amount;
    private LocalDateTime timestamp;
    private String location;
    private String merchantCategory;
    private Boolean isInternational;
}
