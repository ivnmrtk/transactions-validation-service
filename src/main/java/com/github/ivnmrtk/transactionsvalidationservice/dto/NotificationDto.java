package com.github.ivnmrtk.transactionsvalidationservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NotificationDto {
    private Integer transactionId;
    private BigDecimal incomingAmount;
    private BigDecimal savedAmount;
    private ValidationState validationState;
}

