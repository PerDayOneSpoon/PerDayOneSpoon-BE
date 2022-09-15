package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.response.calender.CalenderGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GoalRepositoryCustom  {

    List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId);

    Optional<CountDto> getCountGoal(LocalDateTime currentdate,String socialId);

    List<TodayGoalsDto> getTodayGoal(LocalDateTime currentdate, String socialId);

    List<TodayGoalsDto> getFriendTodayGoal(LocalDateTime currentdate, String socialId, boolean privateCheck);

    List<CalenderGoalsDto> getMyCalender(LocalDate startDate, LocalDate endDate, String socialId);

    List<CalenderGoalsDto> getFriendCalender(LocalDate startDate, LocalDate endDate, boolean privateCheck, String socialId);
}
