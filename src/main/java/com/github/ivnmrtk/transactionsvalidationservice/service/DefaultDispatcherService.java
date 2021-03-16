package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.enumerations.NotificationChannel;
import com.github.ivnmrtk.transactionsvalidationservice.config.properties.NotificationChannelsProperties;
import com.github.ivnmrtk.transactionsvalidationservice.dto.NotificationDto;
import com.github.ivnmrtk.transactionsvalidationservice.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class DefaultDispatcherService extends AbstractDispatcherService<NotificationDto> {

    private final NotificationDtoValidationService transactionValidationService;

    private final List<NotificationChannel> enabledChannels;

    public DefaultDispatcherService(NotificationDtoValidationService transactionValidationService,
                                    NotificationDtoSenderService notificationDtoSenderService,
                                    NotificationChannelsProperties notificationChannelsProperties) {
        this.transactionValidationService = transactionValidationService;
        this.senderService = notificationDtoSenderService;
        this.enabledChannels = notificationChannelsProperties.getEnabledChannels();
    }

    public void validateAndDispatchDefault(final Integer txId, final BigDecimal txAmount) {
        try {
            var notificationDto = transactionValidationService.validateAndCreateNotificationDto(txId, txAmount);
            //Отправка нотификаций во все включенные каналы
            enabledChannels.forEach(channel -> sendToChannel(channel, notificationDto));
        } catch (ValidationException e) {
            log.warn("Stop notification sending due to invalid data of received transaction!");
        }
    }

}
