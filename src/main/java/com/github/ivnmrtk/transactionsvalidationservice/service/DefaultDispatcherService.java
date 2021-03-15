package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.config.properties.NotificationChannel;
import com.github.ivnmrtk.transactionsvalidationservice.config.properties.NotificationChannelsProperties;
import com.github.ivnmrtk.transactionsvalidationservice.dto.NotificationDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DefaultDispatcherService extends AbstractDispatcherService<NotificationDto>  {

    private final NotificationDtoValidationService transactionValidationService;

    private final List<NotificationChannel> enabledChannels;

    public DefaultDispatcherService(NotificationDtoValidationService transactionValidationService,
                                    NotificationDtoSenderService notificationDtoSenderService,
                                    NotificationChannelsProperties notificationChannelsProperties) {
        this.transactionValidationService = transactionValidationService;
        this.senderService = notificationDtoSenderService;
        this.enabledChannels = notificationChannelsProperties.getEnabledChannels();
    }

    public void validateAndDispatch(final Integer txId, final BigDecimal txAmount) {
        var notificationDto = transactionValidationService.validateAndCreateNotificationDto(txId, txAmount);
        enabledChannels.forEach(channel -> sendToChannel(channel, notificationDto));
    }

}
