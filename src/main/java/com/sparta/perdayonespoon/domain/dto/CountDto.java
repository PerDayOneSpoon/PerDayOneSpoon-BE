package com.sparta.perdayonespoon.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

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
