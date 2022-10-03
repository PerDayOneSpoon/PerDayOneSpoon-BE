package com.sparta.perdayonespoon.domain.dto.response.Goal;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class DayGoalsDto {

    private List<SpecificGoalsDto> specificGoalsDtoList;

    private boolean isMe;

    @Builder
    public  DayGoalsDto(List<SpecificGoalsDto> specificGoalsDtoList, boolean isMe){
        this.specificGoalsDtoList = specificGoalsDtoList;
        this.isMe = isMe;
    }
}
