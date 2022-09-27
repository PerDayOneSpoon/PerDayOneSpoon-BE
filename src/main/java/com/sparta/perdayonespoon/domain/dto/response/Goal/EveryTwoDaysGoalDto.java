package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class EveryTwoDaysGoalDto {
    private int currentDay;
    private boolean achievement;

    @QueryProjection
    public EveryTwoDaysGoalDto(LocalDateTime currentDay, boolean achievement){
        this.currentDay=currentDay.getDayOfWeek().getValue();
        this.achievement=achievement;
    }
}
