package com.sparta.perdayonespoon.domain.dto.request;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class CalenderRequestDto {

    private Long memberId;

    @Nullable
    private String calenderDate;

    @Nullable
    private Integer calenderMonth;
}
