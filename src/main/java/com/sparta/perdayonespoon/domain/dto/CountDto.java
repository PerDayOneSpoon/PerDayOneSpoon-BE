package com.sparta.perdayonespoon.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
public class CountDto {

    private String currentdate;

    private long totalcount;

    @QueryProjection
    public CountDto(String currentdate, long totalcount){
        this.currentdate=currentdate;
        this.totalcount=totalcount;
    }
}
