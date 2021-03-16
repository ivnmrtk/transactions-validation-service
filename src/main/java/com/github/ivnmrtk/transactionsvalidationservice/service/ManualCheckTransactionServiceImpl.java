package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.config.properties.KafkaTopicsProperties;
import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalTransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class ManualCheckTransactionServiceImpl implements ManualCheckTransactionService{

    private final KafkaTemplate<Integer, ExternalTransactionDto> kafkaTemplate;

    private final String transactionsTopic;

    public ManualCheckTransactionServiceImpl(KafkaTemplate<Integer, ExternalTransactionDto> kafkaTemplate, KafkaTopicsProperties topicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionsTopic = topicsProperties.getTransactions().getName();
    }

    @Override
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void sendTransaction(ExternalTransactionDto externalTransactionDto) {
        try {
            kafkaTemplate.send(transactionsTopic, externalTransactionDto.getPId(), externalTransactionDto).get();
        } catch (Exception e) {
            log.error("Can't send to kafka transaction {}!", externalTransactionDto, e);
        }
    }

}
