package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MonthGoalsDto {

    private String startDate;
    private String endDate;

    @QueryProjection
    public MonthGoalsDto(LocalDateTime startDate, LocalDateTime endDate){
        this.startDate = startDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.endDate = endDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
    }
}
