package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.QCountDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.QTodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.QCalendarGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.QGoalRateDto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId){
        return queryFactory
                .select(new QGoalRateDto(goal.currentDate.stringValue().substring(0,10), goal.achievementCheck,goal.count()))
                .from(goal)
                .where(goal.currentDate.dayOfMonth().between(sunday.getDayOfMonth(),saturday.getDayOfMonth()),GoalSocialEq(socialId))
                .groupBy(goal.currentDate.stringValue().substring(0,10),goal.achievementCheck)
                .fetch();
    }

    @Override
    public Optional<CountDto> getCountGoal(LocalDateTime currentDate,String socialId){
        return Optional.ofNullable(queryFactory
                .select(new QCountDto(goal.currentDate.stringValue().substring(0,10), goal.count()))
                .from(goal)
                .where(GoalCurrentEq(currentDate.getDayOfMonth()),GoalSocialEq(socialId))
                .groupBy(goal.currentDate.stringValue().substring(0,10))
                .fetchOne());
    }

    @Override
    public List<TodayGoalsDto> getTodayGoal(LocalDateTime currentDate,String socialId){
        return queryFactory.select(new QTodayGoalsDto(goal.title,goal.startDate
                ,goal.endDate,goal.time, goal.characterId,goal.id,goal.privateCheck,
                goal.currentDate,goal.achievementCheck,goal.heartList.size()))
                .from(goal)
                .where(GoalCurrentEq(currentDate.getDayOfMonth()),GoalSocialEq(socialId))
                .fetch();
    }

    @Override
    public List<TodayGoalsDto> getFriendTodayGoal(LocalDateTime currentDate,String socialId,boolean privateCheck){
        return queryFactory.select(new QTodayGoalsDto(goal.title,goal.startDate
                        ,goal.endDate,goal.time, goal.characterId,goal.id,goal.privateCheck,
                        goal.currentDate,goal.achievementCheck,goal.heartList.size()))
                .from(goal)
                .where(GoalCurrentEq(currentDate.getDayOfMonth()),GoalSocialEq(socialId),GoalPrivateEq(privateCheck))
                .fetch();
    }

    @Override
    public List<CalendarGoalsDto> getMyCalendar(LocalDate startDate, LocalDate endDate, String socialId){
        return queryFactory.select(new QCalendarGoalsDto(goal.id,goal.title,goal.startDate, goal.endDate, goal.currentDate,goal.time,goal.characterId,goal.privateCheck,goal.achievementCheck))
                .from(goal)
                .where(goal.currentDate.dayOfMonth().between(startDate.getDayOfMonth(),endDate.getDayOfMonth()),GoalSocialEq(socialId))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    @Override
    public List<CalendarGoalsDto> getFriendCalendar(LocalDate startDate, LocalDate endDate, boolean privateCheck,
                                                    String socialId){
        return queryFactory.select(new QCalendarGoalsDto(goal.id,goal.title,goal.startDate, goal.endDate, goal.currentDate,goal.time,goal.characterId,goal.privateCheck,goal.achievementCheck))
                .from(goal)
                .where(goal.currentDate.dayOfMonth().between(startDate.getDayOfMonth(),endDate.getDayOfMonth()),GoalSocialEq(socialId),GoalPrivateEq(privateCheck))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    private BooleanExpression GoalSocialEq(String socialId) {
        return isEmpty(socialId) ? null : goal.socialId.eq(socialId);
    }

    private BooleanExpression GoalCurrentEq(int currentDate) {
        return isEmpty(currentDate) ? null : goal.currentDate.dayOfMonth().eq(currentDate);
    }

    private BooleanExpression GoalPrivateEq(boolean privateCheck) {
        return isEmpty(privateCheck) ? null : goal.privateCheck.eq(privateCheck);
    }
}
