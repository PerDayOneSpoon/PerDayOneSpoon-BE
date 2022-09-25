package com.sparta.perdayonespoon.domain.dto.response.calendar;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class MonthCalendarDto {

    private Long id;
    private String currentDate;
    private List<String> charactorColorlist;

    @Builder
    public MonthCalendarDto(Long id, String currentDate, List<String> charactorColorlist){
        this.id= id;
        this.currentDate = currentDate;
        this.charactorColorlist =charactorColorlist;
    }
}
