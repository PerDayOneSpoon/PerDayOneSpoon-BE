package com.sparta.perdayonespoon.sse.dto;

import com.sparta.perdayonespoon.sse.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationDto {

    private Long id;

    private NotificationType notificationType;

    private String message;

    private boolean isRead;

    @Builder
    public NotificationDto(Long id,String message, boolean isRead,NotificationType notificationType){
        this.id = id;
        this.message =message;
        this.isRead =isRead;
        this.notificationType = notificationType;
    }
}
