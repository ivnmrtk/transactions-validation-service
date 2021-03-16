package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.config.properties.KafkaTopicsProperties;
import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalTransactionDto;
import com.github.ivnmrtk.transactionsvalidationservice.dto.NotificationDto;
import com.github.ivnmrtk.transactionsvalidationservice.enumerations.ValidationState;
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
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * В данном класе содержится тест, позволяющий провести полное end-to-end тестирование логики приложения:
 * эмулируется запись транзакций в очередь transactions-data-topic и проводится сверка всех результирующих записей,
 * отправленных в очередь notifications-data-topic
 */
@Slf4j
class EndToEndTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private KafkaTemplate<Integer, ExternalTransactionDto> testingKafkaTemplate;

    KafkaMessageListenerContainer<String, NotificationDto> container;

    BlockingQueue<ConsumerRecord<String, NotificationDto>> notificationsTopicData;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("test-tx-validation-svc", "false", embeddedKafkaBroker));
        DefaultKafkaConsumerFactory<String, NotificationDto> consumerFactory =
                new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new JsonDeserializer<>(NotificationDto.class, false));
        ContainerProperties containerProperties =
                new ContainerProperties(kafkaTopicsProperties.getNotifications().getName());
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        notificationsTopicData = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, NotificationDto>) notificationsTopicData::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void fullEndToEndTest() throws ExecutionException, InterruptedException {
        try {
            var testIncomingTxs = generateTestData();
            for (ExternalTransactionDto tx : testIncomingTxs) {
                testingKafkaTemplate.executeInTransaction(t -> t.send(kafkaTopicsProperties.getTransactions().getName(), tx.getPId(), tx)).get();
            }
            //Ждем, покав результирующую очередь будут отправлены все записи нотификаций
            Awaitility.await().atMost(20, TimeUnit.SECONDS).until(() -> notificationsTopicData.size() == testIncomingTxs.size());
            log.info("Final notification queue size: {}", notificationsTopicData.size());
            Assertions.assertFalse(notificationsTopicData.isEmpty());

            //Сверка результатов
            var txListById = notificationsTopicData.stream().filter(it -> it.value().getTransactionId() == 123).collect(Collectors.toList());
            assertEquals(2, txListById.size());
            var firstTx = txListById.stream().findFirst().get().value();
            assertAll(
                    () -> assertEquals(BigDecimal.valueOf(100.05), firstTx.getSavedAmount()),
                    () -> assertEquals(BigDecimal.valueOf(94.7), firstTx.getIncomingAmount()),
                    () -> assertEquals(ValidationState.INCORRECT, firstTx.getValidationState())
            );
            var secondTx =notificationsTopicData.stream().filter(it -> it.value().getTransactionId() == 124)
                    .findFirst().get().value();
            assertAll(
                    () -> assertEquals(BigDecimal.valueOf(150.75), secondTx.getSavedAmount()),
                    () -> assertEquals(BigDecimal.valueOf(150.75), secondTx.getIncomingAmount()),
                    () -> assertEquals(ValidationState.CORRECT, secondTx.getValidationState())
            );
            var thirdTx =notificationsTopicData.stream().filter(it -> it.value().getTransactionId() == 125)
                    .findFirst().get().value();
            assertAll(
                    () -> assertEquals(BigDecimal.valueOf(1010.00).setScale(2), thirdTx.getSavedAmount()),
                    () -> assertEquals(BigDecimal.valueOf(1020.2), thirdTx.getIncomingAmount()),
                    () -> assertEquals(ValidationState.INCORRECT, thirdTx.getValidationState())
            );
            var fourthTx =notificationsTopicData.stream().filter(it -> it.value().getTransactionId() == 126)
                    .findFirst().get().value();
            assertAll(
                    () -> assertEquals(BigDecimal.valueOf(15.5).setScale(2), fourthTx.getSavedAmount()),
                    () -> assertEquals(BigDecimal.valueOf(15.5), fourthTx.getIncomingAmount()),
                    () -> assertEquals(ValidationState.CORRECT, fourthTx.getValidationState())
            );
            var fifthTx =notificationsTopicData.stream().filter(it -> it.value().getTransactionId() == 127)
                    .findFirst().get().value();
            assertAll(
                    () -> assertNull(fifthTx.getSavedAmount()),
                    () -> assertEquals(BigDecimal.valueOf(120.74), fifthTx.getIncomingAmount()),
                    () -> assertEquals(ValidationState.NOT_FOUND, fifthTx.getValidationState())
            );

        } catch (Exception e) {
            log.error("Ar unexpected error occurred during test execution!", e);
            throw e;
        }
    }

    //Инициализация тестовых данных транзакций для процесса сверки
    //Полностью соответствуют заданными в условии ТЗ данным
    private Collection<ExternalTransactionDto> generateTestData() {
        return List.of(
                ExternalTransactionDto.builder().pId(123).pAmount(BigDecimal.valueOf(94.7)).pData(20160101120000L).build(),
                ExternalTransactionDto.builder().pId(123).pAmount(BigDecimal.valueOf(94.7)).pData(20160101120000L).build(),
                ExternalTransactionDto.builder().pId(124).pAmount(BigDecimal.valueOf(150.75)).pData(20160101120001L).build(),
                ExternalTransactionDto.builder().pId(125).pAmount(BigDecimal.valueOf(1020.2)).pData(20160101120002L).build(),
                ExternalTransactionDto.builder().pId(126).pAmount(BigDecimal.valueOf(15.5)).pData(20160101120003L).build(),
                ExternalTransactionDto.builder().pId(127).pAmount(BigDecimal.valueOf(120.74)).pData(20160101120004L).build()
        );
    }

}
