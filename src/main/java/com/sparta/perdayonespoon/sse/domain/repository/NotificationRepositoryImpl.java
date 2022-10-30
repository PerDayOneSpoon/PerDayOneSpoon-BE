package com.sparta.perdayonespoon.sse.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.sse.dto.NotificationDto;
import com.sparta.perdayonespoon.sse.dto.QNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QMember.member;
import static com.sparta.perdayonespoon.sse.domain.entity.QNotification.notification;


@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements  NotificationRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<NotificationDto> getMessageById(Long userId){
        return queryFactory
                .select(new QNotificationDto(notification.id,notification.message,notification.isRead,notification.notificationType))
                .from(notification)
                .innerJoin(notification.receiver, member).fetchJoin()
                .where(member.id.eq(userId), notification.isRead.eq(false))
                .fetch();
    }
    @Override
    public long changeAllMessage(Long id){
        return queryFactory
                .update(notification)
                .set(notification.isRead,true)
                .where(notification.receiver.id.eq(id))
                .execute();
    }

    @Override
    public long changeOneMessage(Long id, Long notificationId){
        return queryFactory
                .update(notification)
                .set(notification.isRead,true)
                .where(notification.receiver.id.eq(id),notification.id.eq(notificationId))
                .execute();
    }
}
