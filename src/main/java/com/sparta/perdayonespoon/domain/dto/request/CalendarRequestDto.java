package com.sparta.perdayonespoon.domain.dto.request;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class CalendarRequestDto {

    private Long memberId;

    @Nullable
    private String calendarDate;

    @Nullable
    private String calendarYearAndMonth;
}
