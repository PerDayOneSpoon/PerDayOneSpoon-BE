package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.QCountDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.QTodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.QGoalRateDto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialid){
        return queryFactory
                .select(new QGoalRateDto(goal.currentDate.stringValue().substring(0,10), goal.achievementCheck,goal.count()))
                .from(goal)
                .where(goal.currentDate.dayOfMonth().between(sunday.getDayOfMonth(),saturday.getDayOfMonth()),GoalSocialEq(socialid))
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
                ,goal.endDate,goal.time,goal.characterId,goal.id,goal.privateCheck, goal.socialId
                ,goal.currentDate,goal.achievementCheck))
                .from(goal)
                .where(GoalCurrentEq(currentDate.getDayOfMonth()),GoalSocialEq(socialId))
                .fetch();

    }

    private BooleanExpression GoalSocialEq(String socialId) {
        return isEmpty(socialId) ? null : goal.socialId.eq(socialId);
    }

    private BooleanExpression GoalCurrentEq(int currentDate) {
        return isEmpty(currentDate) ? null : goal.currentDate.dayOfMonth().eq(currentDate);
    }
}
