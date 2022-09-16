package com.sparta.perdayonespoon.domain.dto.response.calendar;

import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.follow.FriendDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CalendarUniteDto {
    private String startDate;

    private String endDate;

    private List<MonthCalendarDto> monthCalenderDtoList;

    private List<TodayGoalsDto> todayGoalsDtoList;

    private List<FriendDto> peopleList;

    private long code;

    private String msg;
    @Builder
    public CalendarUniteDto(String startDate, String endDate, List<MonthCalendarDto> monthCalenderDtoList,
                            List<TodayGoalsDto> todayGoalsDtoList, MsgDto msgDto, List<FriendDto> peopleList){
        this.startDate =startDate;
        this.endDate=endDate;
        this.monthCalenderDtoList = monthCalenderDtoList;
        this.todayGoalsDtoList =todayGoalsDtoList;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
        this.peopleList = peopleList;
    }
}
