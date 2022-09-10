package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Goal;
//import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.QCountDto;
import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.QGoalRateDto;
//import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
//import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static org.springframework.util.ObjectUtils.isEmpty;

public class GoalRepositoryImpl implements GoalRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public GoalRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialid){
        return queryFactory
                .select(new QGoalRateDto(goal.socialId,goal.currentdate.stringValue().substring(0,10), goal.achievementCheck,goal.count()))
                .from(goal)
                .where(goal.currentdate.dayOfMonth().between(sunday.getDayOfMonth(),saturday.getDayOfMonth()),GoalSocialEq(socialid))
                .groupBy(goal.achievementCheck,goal.currentdate.stringValue().substring(0,10),goal.socialId)
                .fetch();
    }

    @Override
    public Optional<CountDto> getCountGoal(LocalDateTime currentdate){
        return Optional.ofNullable(queryFactory
                .select(new QCountDto(goal.currentdate.stringValue().substring(0, 10), goal.count()))
                .from(goal)
                .where(GoalCurrentEq(currentdate.getDayOfMonth()))
                .groupBy(goal.currentdate.stringValue().substring(0, 10))
                .fetchOne());
    }

    private BooleanExpression GoalSocialEq(String socialId) {
        return isEmpty(socialId) ? null : goal.socialId.eq(socialId);
    }

    private BooleanExpression GoalCurrentEq(int currentdate) {
        return isEmpty(currentdate) ? null : goal.currentdate.dayOfMonth().eq(currentdate);
    }
}
