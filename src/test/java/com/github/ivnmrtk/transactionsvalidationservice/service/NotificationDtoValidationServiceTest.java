package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.enumerations.ValidationState;
import com.github.ivnmrtk.transactionsvalidationservice.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql("/transactions-test-data.sql")
class NotificationDtoValidationServiceTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private NotificationDtoValidationService notificationDtoValidationService;

    @Test
    void correctValidationTest() {
        var amountToCheck = BigDecimal.valueOf(150.7);
        var resultDto = notificationDtoValidationService.validateAndCreateNotificationDto(201, amountToCheck);
        assertNotNull(resultDto);
        assertAll(
                () -> assertEquals(201, resultDto.getTransactionId()),
                () -> assertEquals(amountToCheck, resultDto.getIncomingAmount()),
                () -> assertEquals(BigDecimal.valueOf(150.7).setScale(2), resultDto.getSavedAmount()),
                () -> assertEquals(ValidationState.CORRECT, resultDto.getValidationState())
        );
    }

    @Test
    void incorrectValidationTest() {
        var amountToCheck = BigDecimal.valueOf(152.7);
        var resultDto = notificationDtoValidationService.validateAndCreateNotificationDto(201, amountToCheck);
        assertNotNull(resultDto);
        assertAll(
                () -> assertEquals(201, resultDto.getTransactionId()),
                () -> assertEquals(amountToCheck, resultDto.getIncomingAmount()),
                () -> assertEquals(BigDecimal.valueOf(150.7).setScale(2), resultDto.getSavedAmount()),
                () -> assertEquals(ValidationState.INCORRECT, resultDto.getValidationState())
        );
    }

    @Test
    void notFoundValidationTest() {
        var incomingAmount = BigDecimal.valueOf(150.7);
        var resultDto = notificationDtoValidationService.validateAndCreateNotificationDto(301, incomingAmount);
        assertNotNull(resultDto);
        assertAll(
                () -> assertEquals(301, resultDto.getTransactionId()),
                () -> assertEquals(incomingAmount, resultDto.getIncomingAmount()),
                () -> assertNull(resultDto.getSavedAmount()),
                () -> assertEquals(ValidationState.NOT_FOUND, resultDto.getValidationState())
        );

    }

    @Test
    void exceptionOnNullFieldsTest() {
        assertThrows(ValidationException.class, () -> notificationDtoValidationService.validateAndCreateNotificationDto(201, null));
    }
}
