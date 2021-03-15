package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.config.properties.NotificationChannel;

public abstract class AbstractDispatcherService<T> {

    protected SenderService<T> senderService;

    //Шаблонный метод для отправки нотификаций в выбранный канал
    public void sendToChannel(final NotificationChannel channel, final T notificationDto) {
        switch (channel) {
            case KAFKA:
                senderService.sendToKafka(notificationDto);
            //should be more cases for other channels
        }
    }


}
