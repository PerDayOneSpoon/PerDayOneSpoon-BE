package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;

import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private int heartCnt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String goalFlag;

    private boolean heartCheck;

    @QueryProjection
    public TodayGoalsDto(String title, LocalDateTime startDate, LocalDateTime endDate, String time, int characterUrl,
                         Long id, boolean privateCheck, LocalDateTime currentDate, boolean achievementCheck, int heartCnt,
                         String goalFlag, Boolean heartCheck){
        this.title=title;
        this.startDate= startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.endDate= endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.time=time;
        this.characterUrl= GetCharacterUrl.getMandooUrl(characterUrl);
        this.id=id;
        this.privateCheck=privateCheck;
        this.currentDate= currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.achievementCheck=achievementCheck;
        this.heartCnt = heartCnt;
        this.goalFlag = goalFlag;
        this.heartCheck = heartCheck;
    }
    @Builder
    public TodayGoalsDto(String title, LocalDateTime startDate, LocalDateTime endDate, String time, int characterUrl,
                         Long id, boolean privateCheck, String currentDate, boolean achievementCheck, int heartCnt,
                         String goalFlag){
        this.title=title;
        this.startDate= startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.endDate= endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.time=time;
        this.characterUrl= GetCharacterUrl.getMandooUrl(characterUrl);
        this.id=id;
        this.privateCheck=privateCheck;
        this.currentDate= currentDate;
        this.achievementCheck=achievementCheck;
        this.heartCnt = heartCnt;
        this.goalFlag =goalFlag;
    }
}
