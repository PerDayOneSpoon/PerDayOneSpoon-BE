package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.annotations.QueryProjection;

import com.sparta.perdayonespoon.util.GetCharacterUrl;
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
    private String currentDate;
    private String time;
    private String characterUrl;
    private boolean privateCheck;
    private boolean achievementCheck;

    @QueryProjection
    public TodayGoalsDto(String title, LocalDateTime startDate, LocalDateTime endDate, String time, int characterUrl,
                         Long id, boolean privateCheck, LocalDateTime currentDate, boolean achievementCheck){
        this.title=title;
        this.startDate= startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.endDate= endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.time=time;
        this.characterUrl= GetCharacterUrl.getMandooUrl(characterUrl);
        this.id=id;
        this.privateCheck=privateCheck;
        this.currentDate= currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.achievementCheck=achievementCheck;
    }
    @Builder
    public TodayGoalsDto(String title, LocalDateTime startDate, LocalDateTime endDate, String time, int characterUrl,
                         Long id, boolean privateCheck, String currentDate, boolean achievementCheck){
        this.title=title;
        this.startDate= startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.endDate= endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.time=time;
        this.characterUrl= GetCharacterUrl.getMandooUrl(characterUrl);
        this.id=id;
        this.privateCheck=privateCheck;
        this.currentDate= currentDate;
        this.achievementCheck=achievementCheck;
    }
}
