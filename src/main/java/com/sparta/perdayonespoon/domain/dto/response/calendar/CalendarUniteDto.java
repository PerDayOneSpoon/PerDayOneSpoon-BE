package com.sparta.perdayonespoon.domain.dto.response.calendar;

import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CalendarUniteDto {

    private String mySocialId;
    private String myProfileImage;
    private String startDate;

    private String endDate;

    private List<MonthCalendarDto> monthCalenderDtoList;

    private List<TodayGoalsDto> todayGoalsDtoList;

    private long code;

    private String msg;
    @Builder
    public CalendarUniteDto(String startDate, String endDate, List<MonthCalendarDto> monthCalenderDtoList,
                            String myProfileImage,String mySocialId,List<TodayGoalsDto> todayGoalsDtoList, MsgDto msgDto){
        this.startDate =startDate;
        this.endDate=endDate;
        this.myProfileImage=myProfileImage;
        this.mySocialId=mySocialId;
        this.monthCalenderDtoList = monthCalenderDtoList;
        this.todayGoalsDtoList =todayGoalsDtoList;
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
    }
}
