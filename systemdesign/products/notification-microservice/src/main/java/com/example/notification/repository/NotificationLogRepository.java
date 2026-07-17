package com.example.notification.repository;

import com.example.notification.model.NotificationLog;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.customizer.Bind;
import java.util.List;
import java.time.LocalDateTime;

@RegisterConstructorMapper(NotificationLog.class)
public interface NotificationLogRepository {

    @SqlUpdate("INSERT INTO notification_logs (user_id, template_key, channel, status, recipient_email, recipient_phone, subject, body, error_message) " +
               "VALUES (:userId, :templateKey, :channel, :status, :recipientEmail, :recipientPhone, :subject, :body, :errorMessage)")
    @GetGeneratedKeys
    Long insert(@BindBean NotificationLog log);

    @SqlUpdate("INSERT INTO notification_logs (user_id, template_key, channel, status, recipient_email, recipient_phone, subject, body, error_message, created_at) " +
               "VALUES (:userId, :templateKey, :channel, :status, :recipientEmail, :recipientPhone, :subject, :body, :errorMessage, :createdAt)")
    void insertWithTimestamp(@BindBean NotificationLog log);

    @SqlQuery("SELECT id, user_id, template_key, channel, status, recipient_email, recipient_phone, subject, body, error_message, created_at FROM notification_logs ORDER BY created_at DESC")
    List<NotificationLog> findAllLogs();

    @SqlQuery("SELECT id, user_id, template_key, channel, status, recipient_email, recipient_phone, subject, body, error_message, created_at FROM notification_logs WHERE created_at >= :startDate ORDER BY created_at ASC")
    List<NotificationLog> findLogsSince(@Bind("startDate") LocalDateTime startDate);

    @SqlQuery("SELECT id, user_id, template_key, channel, status, recipient_email, recipient_phone, subject, body, error_message, created_at FROM notification_logs ORDER BY created_at DESC LIMIT :limit")
    List<NotificationLog> findRecentLogs(@Bind("limit") int limit);
}
