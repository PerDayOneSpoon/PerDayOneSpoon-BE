package com.sparta.perdayonespoon.sse.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationDumyDto {
    private String lastEventId;

    private boolean isRead;

    @Builder
    public NotificationDumyDto(String lastEventId){
        this.lastEventId = lastEventId;
        isRead=true;
    }
}
