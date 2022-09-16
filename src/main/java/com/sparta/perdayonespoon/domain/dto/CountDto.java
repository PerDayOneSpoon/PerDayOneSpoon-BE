package com.sparta.perdayonespoon.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class CountDto {

    private String currentDate;

    private long totalCount;

    @QueryProjection
    public CountDto(String currentDate, long totalCount){
        this.currentDate=currentDate;
        this.totalCount=totalCount;
    }
}
