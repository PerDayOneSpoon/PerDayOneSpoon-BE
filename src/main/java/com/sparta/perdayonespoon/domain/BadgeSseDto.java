package com.sparta.perdayonespoon.domain;

import com.sparta.perdayonespoon.sse.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BadgeSseDto {
    private Member member;
    private String message;
    private NotificationType notificationType;

    @Builder
    public BadgeSseDto(Member member, String message, NotificationType notificationType){
        this.member =member;
        this.message=message;
        this.notificationType=notificationType;
    }
}
