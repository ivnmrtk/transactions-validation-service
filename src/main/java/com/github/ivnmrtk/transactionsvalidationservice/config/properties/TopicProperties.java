package com.github.ivnmrtk.transactionsvalidationservice.config.properties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TopicProperties {
    private String name;
    private short replicationFactor;
    private short partitions;
}

