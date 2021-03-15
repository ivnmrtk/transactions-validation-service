package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.config.properties.KafkaTopicsProperties;
import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalKafkaTransactionDto;
import com.github.ivnmrtk.transactionsvalidationservice.dto.NotificationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@EmbeddedKafka(topics = {"${topics.transactions.name}", "${topics.notifications.name}"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = {"offsets.topic.num.partitions=1",
                "transaction.state.log.replication.factor=1",
                "transaction.state.log.min.isr=1",
                "default.replication.factor=1",
                "min.insync.replicas=1"}
)
@Slf4j
public class EndToEndTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private KafkaTemplate<Integer, ExternalKafkaTransactionDto> testingKafkaTemplate;

    KafkaMessageListenerContainer<String, NotificationDto> container;

    BlockingQueue<ConsumerRecord<String, NotificationDto>> notificationsTopicData;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("test-tx-validation-svc", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, NotificationDto> consumerFactory =
                new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new JsonDeserializer<>());
        ContainerProperties containerProperties =
                new ContainerProperties(kafkaTopicsProperties.getNotifications().getName());
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        notificationsTopicData = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, NotificationDto>) notificationsTopicData::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void fullEndToEndTest() {
        try {
            var testIncomingTxs = generateTestData();
            for (ExternalKafkaTransactionDto tx : testIncomingTxs) {
                testingKafkaTemplate.executeInTransaction(t -> t.send(kafkaTopicsProperties.getTransactions().getName(), tx.getPid(), tx)).get();
            }
            Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> notificationsTopicData.size() == testIncomingTxs.size());
            log.info("Size: {}", notificationsTopicData.size());
            Assertions.assertFalse(notificationsTopicData.isEmpty());
        } catch (Exception e) {
            log.error("");
        }


    }

    private Collection<ExternalKafkaTransactionDto> generateTestData() {
        return List.of(
                ExternalKafkaTransactionDto.builder().pid(123).pAmount(BigDecimal.valueOf(94.7d)).pData(20160101120000L).build(),
                ExternalKafkaTransactionDto.builder().pid(123).pAmount(BigDecimal.valueOf(94.7d)).pData(20160101120000L).build(),
                ExternalKafkaTransactionDto.builder().pid(124).pAmount(BigDecimal.valueOf(150.75d)).pData(20160101120001L).build(),
                ExternalKafkaTransactionDto.builder().pid(125).pAmount(BigDecimal.valueOf(1020.2d)).pData(20160101120002L).build(),
                ExternalKafkaTransactionDto.builder().pid(126).pAmount(BigDecimal.valueOf(15.5d)).pData(20160101120003L).build(),
                ExternalKafkaTransactionDto.builder().pid(127).pAmount(BigDecimal.valueOf(120.74d)).pData(20160101120004L).build()
        );
    }

}
