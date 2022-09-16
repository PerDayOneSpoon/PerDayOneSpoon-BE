package com.sparta.perdayonespoon.domain.dto.response.rate;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
public class GoalRateDto {
    @ApiModelProperty(example = "주간 달성률")
    private double rate;
    @ApiModelProperty(example = "목표의 요일")
    private String dayString;
    @ApiModelProperty(example = "목표 달성 여부")
    private boolean checkGoal;
    private int whatsDay;

    @Transient
    @ApiModelProperty(example = "전체 목표 개수")
    private long totalcount;

    @QueryProjection
    public GoalRateDto(String dayofweek , boolean checkGoal, long totalcount){
//        dayString = dayofweek.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        whatsDay = LocalDate.parse(dayofweek).getDayOfWeek().getValue();
        dayString = LocalDate.parse(dayofweek).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        this.checkGoal=checkGoal;
        this.totalcount=totalcount;
    }
    @Builder
    public GoalRateDto(double rate, String dayString){
        this.rate = rate;
        this.dayString = dayString;
    }

    public WeekRateDto getWeekRateDto(){
        if(whatsDay == 7)
            whatsDay =0;
        return WeekRateDto.builder()
                .id(whatsDay)
                .rate(rate)
                .dayString(dayString)
                .build();
    }
}
