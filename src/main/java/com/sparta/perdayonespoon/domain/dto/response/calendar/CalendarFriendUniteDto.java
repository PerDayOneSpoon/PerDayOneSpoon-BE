package com.sparta.perdayonespoon.domain.dto.response.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
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


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TodayGoalsDto> todayGoalsDtoList;

    private long code;

    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isMe;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean heartCheck;
    @Builder
    public CalendarFriendUniteDto(String startDate, String endDate, List<MonthCalendarDto> monthCalenderDtoList,
                            List<TodayGoalsDto> todayGoalsDtoList, MsgDto msgDto, Boolean isMe,Boolean heartCheck){
        this.startDate =startDate;
        this.endDate=endDate;
        this.monthCalenderDtoList = monthCalenderDtoList;
        this.todayGoalsDtoList =todayGoalsDtoList;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
        this.isMe = isMe;
        this.heartCheck=heartCheck;
    }
}
