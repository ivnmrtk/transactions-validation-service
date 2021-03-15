package com.github.ivnmrtk.transactionsvalidationservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Setter
@Getter
@ConfigurationProperties("topics")
public class KafkaTopicsProperties {

    private TopicProperties transactions;

    private TopicProperties notifications;
}

