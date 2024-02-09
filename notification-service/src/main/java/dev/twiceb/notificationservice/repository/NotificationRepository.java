package dev.twiceb.notificationservice.repository;

import dev.twiceb.notificationservice.model.Notification;
import dev.twiceb.notificationservice.repository.projection.UserNotificationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT noti FROM Notification noti WHERE noti.userId = :userId AND noti.isRead = false")
    Page<UserNotificationProjection> getNotificationsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification noti SET noti.isRead = true WHERE noti.userId = :userId")
    void updateAllNotificationReadState(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification noti SET noti.isRead = true WHERE noti.id = :notificationId AND noti.userId = :userId")
    void updateNotificationReadState(@Param("notificationId") Long notificationId, @Param("userId") Long userId);
}
