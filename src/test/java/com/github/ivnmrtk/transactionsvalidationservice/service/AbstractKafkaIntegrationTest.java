package com.github.ivnmrtk.transactionsvalidationservice.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@EmbeddedKafka(topics = {"${topics.transactions.name}", "${topics.notifications.name}"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = {"offsets.topic.num.partitions=1",
                "transaction.state.log.replication.factor=1",
                "transaction.state.log.min.isr=1",
                "default.replication.factor=1",
                "min.insync.replicas=1"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest
public abstract class AbstractKafkaIntegrationTest {
}
