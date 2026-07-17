package com.example.notification.repository;

import com.example.notification.model.InAppNotification;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterConstructorMapper(InAppNotification.class)
public interface InAppNotificationRepository {

    @SqlQuery("SELECT id, user_id, title, content, is_read AS read, created_at FROM in_app_notifications WHERE user_id = :userId ORDER BY created_at DESC")
    List<InAppNotification> findByUserId(@Bind("userId") String userId);

    @SqlQuery("SELECT id, user_id, title, content, is_read AS read, created_at FROM in_app_notifications WHERE id = :id")
    Optional<InAppNotification> findById(@Bind("id") Long id);

    @SqlUpdate("INSERT INTO in_app_notifications (user_id, title, content, is_read) " +
               "VALUES (:userId, :title, :content, :read)")
    @GetGeneratedKeys
    Long insert(@BindBean InAppNotification notification);

    @SqlUpdate("UPDATE in_app_notifications SET is_read = :read WHERE id = :id")
    int markAsRead(@Bind("id") Long id, @Bind("read") boolean read);
}
