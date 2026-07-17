package com.example.notification.config;

import com.example.notification.repository.InAppNotificationRepository;
import com.example.notification.repository.NotificationLogRepository;
import com.example.notification.repository.NotificationSettingsRepository;
import com.example.notification.repository.NotificationTemplateRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.spring.SpringConnectionFactory;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbiConfig {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(new SpringConnectionFactory(dataSource))
                .installPlugin(new SqlObjectPlugin());
    }

    @Bean
    public NotificationTemplateRepository notificationTemplateRepository(Jdbi jdbi) {
        return jdbi.onDemand(NotificationTemplateRepository.class);
    }

    @Bean
    public NotificationSettingsRepository notificationSettingsRepository(Jdbi jdbi) {
        return jdbi.onDemand(NotificationSettingsRepository.class);
    }

    @Bean
    public InAppNotificationRepository inAppNotificationRepository(Jdbi jdbi) {
        return jdbi.onDemand(InAppNotificationRepository.class);
    }

    @Bean
    public NotificationLogRepository notificationLogRepository(Jdbi jdbi) {
        return jdbi.onDemand(NotificationLogRepository.class);
    }
}
