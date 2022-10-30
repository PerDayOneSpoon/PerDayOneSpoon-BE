package com.sparta.perdayonespoon.sse.domain.repository;

import com.sparta.perdayonespoon.sse.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long>, NotificationRepositoryCustom {

//    @Query("select n from Notification n where n.receiver.id = :userId order by n.id desc")
//    List<Notification> findAllByUserId(Long userId);

    @Query("select count(n) from Notification n where n.receiver.id = :userId and n.isRead = false")
    Long countUnReadNotifications( Long userId);

    Optional<Notification> findById(Long NotificationsId);

    void deleteAllByReceiverId(Long receiverId);

    void deleteById(Long notificationId);

}
