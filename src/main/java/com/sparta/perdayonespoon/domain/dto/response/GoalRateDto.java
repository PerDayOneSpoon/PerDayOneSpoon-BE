package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
public class GoalRateDto {
    @ApiModelProperty(example = "주간 달성률")
    private double rate;
    @ApiModelProperty(example = "목표 만든 사람의 socialId")
    private String socialId;
    @ApiModelProperty(example = "목표의 요일")
    private String dayString;
    @ApiModelProperty(example = "목표 달성 여부")
    private boolean checkGoal;
    @ApiModelProperty(example = "전체 목표 개수")
    private long totalcount;

    @ApiModelProperty(example = "통신 성공 시 서버에서 보내주는 code")
    private long code;

    @ApiModelProperty(example = "통신 성공 시 서버에서 보내주는 메세지")
    private String Msg;

    @QueryProjection
    public GoalRateDto(String socialId, String dayofweek , boolean checkGoal, long totalcount){
        this.socialId=socialId;
//        dayString = dayofweek.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        dayString = LocalDate.parse(dayofweek).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        this.checkGoal=checkGoal;
        this.totalcount=totalcount;
    }

    public void SetTwoField(MsgDto msgDto){
        this.code = msgDto.getCode();
        this.Msg = msgDto.getMsg();
    }
}
