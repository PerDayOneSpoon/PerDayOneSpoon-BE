package com.sparta.perdayonespoon.domain.dto.response.calender;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class MonthCalenderDto {

    private Long id;
    private String currentDate;

    private List<String> charactorColorlist;

    @Builder
    public MonthCalenderDto(Long id, String currentDate, List<String> charactorColorlist){
        this.id= id;
        this.currentDate = currentDate;
        this.charactorColorlist =charactorColorlist;
    }
}
