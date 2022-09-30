package com.sparta.perdayonespoon.sse.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationCountDto {
    private Long count;

    @Builder
    public NotificationCountDto(Long count){
        this.count = count;
    }
}