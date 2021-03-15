package com.github.ivnmrtk.transactionsvalidationservice.service;

/**
 * Интерфейс отправки отчета сверки транзакции определенного типа по каналам нотификации
 * @param <T> тип отправляемого отчета
 */
public interface SenderService<T> {
    /**
     * Отправка отчета сверки транзакции в очередь kafka
     * @param notification - данные отчета
     */
    void sendToKafka(T notification);
}
