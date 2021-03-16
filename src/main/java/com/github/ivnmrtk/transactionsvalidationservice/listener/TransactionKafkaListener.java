package com.github.ivnmrtk.transactionsvalidationservice.listener;

import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalTransactionDto;
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
    public void listenTransactions(final ExternalTransactionDto externalTransactionDto, Acknowledgment ack) {
        if (externalTransactionDto == null) {
            log.warn("Received transactionKafkaDto is null");
            return;
        }
        log.info("Received transaction: {}", externalTransactionDto);
        defaultDispatcherService.validateAndDispatchDefault(
                externalTransactionDto.getPId(),
                externalTransactionDto.getPAmount());
        ack.acknowledge();
    }
}
