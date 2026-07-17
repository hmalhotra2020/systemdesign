package com.example.notification.repository;

import com.example.notification.model.NotificationSettings;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

@RegisterConstructorMapper(NotificationSettings.class)
public interface NotificationSettingsRepository {

    @SqlQuery("SELECT * FROM notification_settings WHERE user_id = :userId")
    Optional<NotificationSettings> findByUserId(@Bind("userId") String userId);

    @SqlUpdate("MERGE INTO notification_settings (user_id, email_enabled, sms_enabled, in_app_enabled) " +
               "KEY (user_id) " +
               "VALUES (:userId, :emailEnabled, :smsEnabled, :inAppEnabled)")
    void save(@BindBean NotificationSettings settings);
}
