package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.config.properties.KafkaTopicsProperties;
import com.github.ivnmrtk.transactionsvalidationservice.dto.NotificationDto;
import com.github.ivnmrtk.transactionsvalidationservice.exception.KafkaSenderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Реализация интерфейска отправки нотификаций по каналам для типа NotificationDto
 */
@Slf4j
@Service
public class NotificationDtoSenderService implements SenderService<NotificationDto> {

    private final KafkaTemplate<Integer, NotificationDto> notificationDtoKafkaTemplate;

    private final String notificationTopicName;

    public NotificationDtoSenderService(KafkaTemplate<Integer, NotificationDto> notificationDtoKafkaTemplate,
                                        KafkaTopicsProperties topicsProperties) {
        this.notificationDtoKafkaTemplate = notificationDtoKafkaTemplate;
        this.notificationTopicName = topicsProperties.getNotifications().getName();
    }

    @Override
    public void sendToKafka(final NotificationDto notification) {
        try {
            notificationDtoKafkaTemplate.send(notificationTopicName, notification.getTransactionId(), notification).get();
            log.info("Message with notification: {} was successfully sent to topic: {}", notification, notificationTopicName);
        } catch (Exception e) {
            log.error("Error while sending notification: {}", notification, e);
            throw new KafkaSenderException(e);
        }
    }
}
