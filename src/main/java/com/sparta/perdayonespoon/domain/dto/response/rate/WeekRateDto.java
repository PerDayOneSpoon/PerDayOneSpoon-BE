package com.sparta.perdayonespoon.domain.dto.response.rate;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class WeekRateDto {

    @ApiModelProperty(example = "주간 달성률")
    private double rate;
    @ApiModelProperty(example = "목표의 요일")
    private String dayString;

    @Builder
    public WeekRateDto(double rate, String dayString){
        this.rate=rate;
        this.dayString=dayString;
    }
}
