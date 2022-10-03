package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.EveryTwoDaysGoalDto;
import com.sparta.perdayonespoon.domain.dto.response.PrivateBadgeCheckDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GoalRepositoryCustom  {

    List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId);

    Optional<CountDto> getCountGoal(LocalDateTime currentDate,String socialId);

    List<TodayGoalsDto> getTodayGoal(LocalDateTime currentDate, String socialId);

    List<Goal> getMyTodayGoal(LocalDateTime localDateTime, String socialId);

    List<TodayGoalsDto> getFriendTodayGoal(LocalDateTime currentDate, Long friendId,String socialId, boolean privateCheck);

    List<Goal> getFollwerTodayGoal(LocalDateTime localDateTime, Long friendId, boolean privateCheck);

    List<CalendarGoalsDto> getMyCalendar(LocalDate startDate, LocalDate endDate, String socialId);

    List<CalendarGoalsDto> getSpecificCalender(LocalDate startDate, LocalDate endDate,String socialId);

    List<CalendarGoalsDto> getFriendCalendar(LocalDate startDate, LocalDate endDate, boolean privateCheck, Long goalId);

    List<CalendarGoalsDto> getFriendSpecificCalendar(LocalDate startDate, LocalDate endDate,boolean privateCheck, Long goalId);

    List<Goal> getCategoryGoals(String socialId, String deleteFlag);

    Optional<Goal> findOldGoal(Long id, String socialId);
}
