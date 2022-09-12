package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class TodayGoalsDto {

    private Long id;
    private String title;
    private String startDate;
    private String endDate;
    private String currentdate;
    private String time;
    private long CharacterId;
    private boolean privateCheck;
    private boolean achievementCheck;
    private String socialId;

    @Builder
    @QueryProjection
    public TodayGoalsDto(String title, LocalDateTime startDate, LocalDateTime endDate, String time, long characterId,
                         Long id, boolean privateCheck, String socialId, LocalDateTime currentdate, boolean achievementCheck){
        this.title=title;
        this.startDate= startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.endDate= endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.time=time;
        this.CharacterId=characterId;
        this.id=id;
        this.privateCheck=privateCheck;
        this.socialId = socialId;
        this.currentdate= currentdate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.achievementCheck=achievementCheck;
    }
}
