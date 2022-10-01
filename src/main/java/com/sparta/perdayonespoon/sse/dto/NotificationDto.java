package com.sparta.perdayonespoon.sse.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.sse.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationDto {

    private Long id;

    private NotificationType notificationType;

    private String message;

    private boolean isRead;

    private LocalDateTime realTime;

    @Builder
    @QueryProjection
    public NotificationDto(Long id,String message, boolean isRead,NotificationType notificationType){
        this.id = id;
        this.message =message;
        this.isRead =isRead;
        this.notificationType = notificationType;
        realTime = LocalDateTime.now();
    }
}
