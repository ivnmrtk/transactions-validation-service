package com.github.ivnmrtk.transactionsvalidationservice.listener;

import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalKafkaTransactionDto;
import com.github.ivnmrtk.transactionsvalidationservice.service.DefaultDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionKafkaListener {

    private final DefaultDispatcherService defaultDispatcherService;

    @KafkaListener(topics = "${topics.transactions.name}")
    public void listenTransactions(final ExternalKafkaTransactionDto externalKafkaTransactionDto, Acknowledgment ack) {
        if (externalKafkaTransactionDto == null) {
            log.warn("Received transactionKafkaDto is null");
            return;
        }
        log.info("Received transaction: {}", externalKafkaTransactionDto);
        defaultDispatcherService.validateAndDispatch(
                externalKafkaTransactionDto.getPid(),
                externalKafkaTransactionDto.getPAmount());
        ack.acknowledge();
    }
}
