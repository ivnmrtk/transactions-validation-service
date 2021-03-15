package com.github.ivnmrtk.transactionsvalidationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class NotificationDto {
    private Integer transactionId;
    private BigDecimal incomingAmount;
    private BigDecimal savedAmount;
    private ValidationState validationState;
}

