package com.sparta.perdayonespoon.domain.dto.response.calendar;

import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CalendarFriendUniteDto {
    private String startDate;

    private String endDate;

    private List<MonthCalendarDto> monthCalenderDtoList;

    private List<TodayGoalsDto> todayGoalsDtoList;

    private long code;

    private String msg;
    @Builder
    public CalendarFriendUniteDto(String startDate, String endDate, List<MonthCalendarDto> monthCalenderDtoList,
                            List<TodayGoalsDto> todayGoalsDtoList, MsgDto msgDto){
        this.startDate =startDate;
        this.endDate=endDate;
        this.monthCalenderDtoList = monthCalenderDtoList;
        this.todayGoalsDtoList =todayGoalsDtoList;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
    }
}
