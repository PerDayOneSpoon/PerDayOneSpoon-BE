package com.sparta.perdayonespoon.domain.dto.response.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.follow.FriendDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CalendarUniteDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String startDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String endDate;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MonthCalendarDto> monthCalenderDtoList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TodayGoalsDto> todayGoalsDtoList;

    private List<FriendDto> peopleList;

    private long code;

    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isMe;

    @Builder
    public CalendarUniteDto(String startDate, String endDate, List<MonthCalendarDto> monthCalenderDtoList,
                            List<TodayGoalsDto> todayGoalsDtoList, MsgDto msgDto, List<FriendDto> peopleList,boolean isMe){
        this.startDate =startDate;
        this.endDate=endDate;
        this.monthCalenderDtoList = monthCalenderDtoList;
        this.todayGoalsDtoList =todayGoalsDtoList;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
        this.peopleList = peopleList;
        this.isMe = isMe;
    }
}
