package com.sparta.perdayonespoon.sse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum NotificationType {

    Badge("badgeAlarm") ,
    Complete("habitAlarm"),
    Follower("followerAlarm"),
    Heart("heartAlarm"),
    Notice("noticeAlarm");

    private String typeMsg;

    NotificationType(String typeMsg){
        this.typeMsg=typeMsg;
    }
}
