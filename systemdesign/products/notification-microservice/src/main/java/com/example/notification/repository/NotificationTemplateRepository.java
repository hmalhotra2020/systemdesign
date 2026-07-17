package com.example.notification.repository;

import com.example.notification.model.NotificationTemplate;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(NotificationTemplate.class)
public interface NotificationTemplateRepository {

    @SqlQuery("SELECT * FROM notification_templates ORDER BY template_key, channel")
    List<NotificationTemplate> findAll();

    @SqlQuery("SELECT * FROM notification_templates WHERE template_key = :key")
    List<NotificationTemplate> findByKey(@Bind("key") String key);

    @SqlQuery("SELECT * FROM notification_templates WHERE template_key = :key AND channel = :channel")
    Optional<NotificationTemplate> findByKeyAndChannel(@Bind("key") String key, @Bind("channel") String channel);

    @SqlUpdate("MERGE INTO notification_templates (template_key, channel, subject_template, body_template) " +
               "KEY (template_key, channel) " +
               "VALUES (:templateKey, :channel, :subjectTemplate, :bodyTemplate)")
    void save(@BindBean NotificationTemplate template);

    @SqlUpdate("DELETE FROM notification_templates WHERE template_key = :key AND channel = :channel")
    int delete(@Bind("key") String key, @Bind("channel") String channel);
}
