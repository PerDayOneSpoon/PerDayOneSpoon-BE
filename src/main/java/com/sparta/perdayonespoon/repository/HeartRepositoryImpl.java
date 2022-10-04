package com.sparta.perdayonespoon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Goal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QBadge.badge;
import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static com.sparta.perdayonespoon.domain.QHeart.heart;
import static com.sparta.perdayonespoon.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class HeartRepositoryImpl implements HeartRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Goal> findGoalsHeart(String goalFlag){
        return jpaQueryFactory
                .selectDistinct(goal)
                .from(goal)
                .where(goal.goalFlag.eq(goalFlag))
                .innerJoin(goal.member, member).fetchJoin()
                .leftJoin(member.badgeList,badge)
                .leftJoin(goal.heartList,heart).fetchJoin()
                .fetch();
    }

}
