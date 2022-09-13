package com.sparta.perdayonespoon.domain.dto.response.rate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class WeekRateDto {

    private int id;
    @ApiModelProperty(example = "주간 달성률")
    private double rate;
    @ApiModelProperty(example = "목표의 요일")
    private String dayString;

    @Builder
    public WeekRateDto(int id,double rate, String dayString){
        this.id=id;
        this.rate=rate;
        this.dayString=dayString;
    }
}
