package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateBadgeCheckDto {

    private String goalFlag;

    @QueryProjection
    public PrivateBadgeCheckDto(String goalFlag){
        this.goalFlag = goalFlag;
    }
}
