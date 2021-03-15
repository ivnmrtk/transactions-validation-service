package com.github.ivnmrtk.transactionsvalidationservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Setter
@Getter
@ConfigurationProperties("notification-channels")
public class NotificationChannelsProperties {
    private List<NotificationChannel> enabledChannels;
}
