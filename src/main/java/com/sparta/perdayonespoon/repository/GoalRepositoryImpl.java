package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Goal;
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
import static com.sparta.perdayonespoon.domain.QMember.member;
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
                goal.currentDate,goal.achievementCheck,goal.heartList.size(),goal.goalFlag
                , new CaseBuilder()
                        .when(goal.socialId.eq(socialId)).then(true)
                        .otherwise(false)
                ))
                .from(goal)
                .where(GoalCurrentEq(currentDate.getDayOfMonth()),GoalSocialEq(socialId))
                .fetch();
    }

    @Override
    public List<TodayGoalsDto> getFriendTodayGoal(LocalDateTime currentDate,Long friendId,boolean privateCheck){
        return queryFactory.select(new QTodayGoalsDto(goal.title,goal.startDate
                        ,goal.endDate,goal.time, goal.characterId,goal.id,goal.privateCheck,
                        goal.currentDate,goal.achievementCheck,goal.heartList.size(),goal.goalFlag,
                        new CaseBuilder()
                                .when(member.id.eq(friendId)).then(true)
                                .otherwise(false)
                        ))
                .from(goal)
                .rightJoin(member).on(goal.socialId.eq(member.socialId),member.id.eq(friendId))
                .where(GoalCurrentEq(currentDate.getDayOfMonth()),GoalPrivateEq(privateCheck))
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
    public List<CalendarGoalsDto> getSpecificCalender(LocalDate startDate, LocalDate endDate, LocalDate middleDate ,String socialId){
        return queryFactory.select(new QCalendarGoalsDto(goal.id,goal.title,goal.startDate, goal.endDate, goal.currentDate,goal.time,goal.characterId,goal.privateCheck,goal.achievementCheck))
                .from(goal)
                .where(goal.currentDate.dayOfMonth().between(startDate.getDayOfMonth(),endDate.getDayOfMonth()),goal.currentDate.month().eq(middleDate.getMonthValue()),GoalSocialEq(socialId))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }


    @Override
    public List<CalendarGoalsDto> getFriendCalendar(LocalDate startDate, LocalDate endDate, boolean privateCheck,
                                                    Long goalId){
        return queryFactory.select(new QCalendarGoalsDto(goal.id,goal.title,goal.startDate, goal.endDate, goal.currentDate,goal.time,goal.characterId,goal.privateCheck,goal.achievementCheck))
                .from(goal)
                .rightJoin(member).on(goal.socialId.eq(member.socialId),member.id.eq(goalId))
                .where(goal.currentDate.dayOfMonth().between(startDate.getDayOfMonth(),endDate.getDayOfMonth()),GoalPrivateEq(privateCheck))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    @Override
    public List<CalendarGoalsDto> getFriendSpecificCalendar(LocalDate startDate, LocalDate endDate,LocalDate middleDate,boolean privateCheck, Long goalId){
        return queryFactory.select(new QCalendarGoalsDto(goal.id,goal.title,goal.startDate, goal.endDate, goal.currentDate,goal.time,goal.characterId,goal.privateCheck,goal.achievementCheck))
                .from(goal)
                .rightJoin(member).on(goal.socialId.eq(member.socialId),member.id.eq(goalId))
                .where(goal.currentDate.dayOfMonth().between(startDate.getDayOfMonth(),endDate.getDayOfMonth()),GoalPrivateEq(privateCheck),goal.currentDate.month().eq(middleDate.getMonthValue()))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    @Override
    public List<Goal> getCategoryGoals(String socialId, String deleteFlag){
        return queryFactory
                .selectFrom(goal)
                .where(GoalSocialEq(socialId),GoalFlagEq(deleteFlag))
                .fetch();
    }

    private BooleanExpression GoalIdEq(Long goalId) {
        return isEmpty(goalId) ? null : goal.id.eq(goalId);
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

    private BooleanExpression GoalFlagEq(String goalFlag){
        return isEmpty(goalFlag) ? null : goal.goalFlag.eq(goalFlag);
    }
}
