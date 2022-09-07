package com.sparta.perdayonespoon.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CalenderResponseDto {

    List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();

    List<MonthResponseDto> monthResponseDtos = new ArrayList<>();
}
