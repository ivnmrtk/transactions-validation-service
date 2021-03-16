package com.github.ivnmrtk.transactionsvalidationservice.config;
import com.github.ivnmrtk.transactionsvalidationservice.config.properties.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Creates topics (if they doesn't exist) used in this service with specified configuration
 */
@Configuration
@RequiredArgsConstructor
public class KafkaTopicsConfiguration {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Bean
    public NewTopic transactionsTopic() {
        var transactionTopicProperties = kafkaTopicsProperties.getTransactions();
        return TopicBuilder.name(transactionTopicProperties.getName())
                .replicas(transactionTopicProperties.getReplicationFactor())
                .partitions(transactionTopicProperties.getPartitions())
                .build();
    }

    @Bean
    public NewTopic notificationTopic() {
        var notificationTopicProperties = kafkaTopicsProperties.getTransactions();
        return TopicBuilder.name(notificationTopicProperties.getName())
                .replicas(notificationTopicProperties.getReplicationFactor())
                .partitions(notificationTopicProperties.getPartitions())
                .build();
    }

}

