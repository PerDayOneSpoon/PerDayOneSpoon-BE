package com.sparta.perdayonespoon.domain.dto.response;


import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.WeekRateDto;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CalenderResponseDto {

    @Builder.Default
    private List<WeekRateDto> weekRateDtoList;
    @Builder.Default
    private List<TodayGoalsDto> todayGoalsDtoList;

    private String weekStartDate;

    private String weekEndDate;
    private String currentDate;
    private long code;
    private String msg;

    @Builder
    public CalenderResponseDto(List<WeekRateDto>weekRateDtoList, List<TodayGoalsDto> todayGoalsDtoList , MsgDto msgDto
                               , String weekStartDate, String weekEndDate, String currentDate){
        this.weekRateDtoList = weekRateDtoList;
        this.todayGoalsDtoList = todayGoalsDtoList;
        this.weekStartDate =weekStartDate;
        this.weekEndDate=weekEndDate;
        this.currentDate=currentDate;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
    }
}
