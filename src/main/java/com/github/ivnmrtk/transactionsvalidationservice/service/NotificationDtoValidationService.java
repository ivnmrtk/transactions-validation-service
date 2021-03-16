package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.dto.NotificationDto;
import com.github.ivnmrtk.transactionsvalidationservice.enumerations.ValidationState;
import com.github.ivnmrtk.transactionsvalidationservice.exception.ValidationException;
import com.github.ivnmrtk.transactionsvalidationservice.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationDtoValidationService {

    private final TransactionsRepository transactionsRepository;

    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public NotificationDto validateAndCreateNotificationDto(final Integer txId, final BigDecimal txAmount) {
        if (txId == null || txAmount == null) {
            log.warn("Some of passed fields are null, txId: {}, txAmount: {}", txId, txAmount);
            throw new ValidationException("Transaction's payload is incorrect!");
        }
        final var optionalTransaction = transactionsRepository.findById(txId);
        final var notificationDto = new NotificationDto();
        notificationDto.setTransactionId(txId);
        notificationDto.setIncomingAmount(txAmount);
        if (optionalTransaction.isEmpty()) {
            //Проставляем специальный статус для необнаруженных в БД транзакций
            notificationDto.setValidationState(ValidationState.NOT_FOUND);
        } else {
            final var savedTransaction = optionalTransaction.get();
            notificationDto.setSavedAmount(savedTransaction.getAmount());
            //Сверка транзакции по сумме и проставления статуса результата сверки
            if (txAmount.compareTo(savedTransaction.getAmount()) == 0) {
                notificationDto.setValidationState(ValidationState.CORRECT);
            } else {
                notificationDto.setValidationState(ValidationState.INCORRECT);
            }
        }
        return notificationDto;
    }

}
