package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.QGoalRateDto;
//import com.sparta.perdayonespoon.domain.dto.response.GoalRateDto;
//import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

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
                .select(new QGoalRateDto(goal.socialId,goal.createdAt.dayOfWeek(), goal.achievementCheck,goal.count()))
                .from(goal)
                .where(goal.createdAt.dayOfMonth().between(sunday.getDayOfMonth(),saturday.getDayOfMonth()),GoalSocialEq(socialid))
                .groupBy(goal.achievementCheck,goal.createdAt.dayOfWeek(),goal.socialId)
                .fetch();
    }

    private BooleanExpression GoalSocialEq(String socialId) {
        return isEmpty(socialId) ? null : goal.socialId.eq(socialId);
    }
}
