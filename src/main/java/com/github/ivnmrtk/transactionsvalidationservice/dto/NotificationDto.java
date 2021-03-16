package com.github.ivnmrtk.transactionsvalidationservice.dto;

import com.github.ivnmrtk.transactionsvalidationservice.enumerations.ValidationState;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Dto для отправки результата сверки транзакции
 */
@Data
public class NotificationDto {
    /**
    id транзакции
     **/
    private Integer transactionId;
    /**
     * Сумма транзакции полученная из внешнего источника
     */
    private BigDecimal incomingAmount;
    /**
     * Сохраненная в БД умма транзакции
     */
    private BigDecimal savedAmount;
    /**
     * Статус валидации транзакции
     */
    private ValidationState validationState;
}

