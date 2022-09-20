package com.sparta.perdayonespoon.domain.dto.response.Goal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class DayGoalsDto {

    private List<TodayGoalsDto> todayGoalsDtoList;

    private boolean isMe;

    @Builder
    public  DayGoalsDto(List<TodayGoalsDto> todayGoalsDtoList, boolean isMe){
        this.todayGoalsDtoList = todayGoalsDtoList;
        this. isMe = isMe;
    }
}
