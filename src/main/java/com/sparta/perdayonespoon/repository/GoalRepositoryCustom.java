package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.MonthGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GoalRepositoryCustom  {

    List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId);

    Optional<CountDto> getCountGoal(LocalDateTime currentdate,String socialId);

    List<TodayGoalsDto> getTodayGoal(LocalDateTime currentdate,String socialId);

    List<MonthGoalsDto> getMonthGoal(LocalDate startDate, LocalDate endDate, String socialId);
}
