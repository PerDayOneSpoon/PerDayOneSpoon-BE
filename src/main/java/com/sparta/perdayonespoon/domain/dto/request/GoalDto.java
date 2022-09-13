package com.sparta.perdayonespoon.domain.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ApiModel(description = "목표를 만들당시 입력한 변수를 감싼 객체명")
@Data
public class GoalDto {
    @ApiModelProperty(notes = "목표를 만들당시 입력한 제목" , example = "사용자가 입력한 목표의 제목")
    public String title;

    @ApiModelProperty(notes = "목표 생성시 선택한 시간",example = "사용자가 입력한 목표달성 시간")
    public String time;
    @ApiModelProperty(name= "characterId", value = "캐릭터의 Id" , example = "6")
    public int characterId;
    @ApiModelProperty(notes = "목표 생성시 선택한 일수", example = "6")
    public int category;

    @ApiModelProperty(name= "privateCheck" ,dataType = "boolean" , notes = "목표 생성시 공개설정,비공개 설정 입력한 것", example = "true")
    public boolean privateCheck;

    @ApiModelProperty(name= "achievementCheck" ,dataType = "boolean" , notes = "목표 완료 여부", example = "true")
    public boolean achievementCheck;
}
