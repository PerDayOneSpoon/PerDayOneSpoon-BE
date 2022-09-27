package com.sparta.perdayonespoon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalsAndHeart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static com.sparta.perdayonespoon.domain.QHeart.heart;

@Repository
@RequiredArgsConstructor
public class HeartRepositoryImpl implements HeartRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public GoalsAndHeart findGoalsHeart(String goalFlag,String socialId){
        List<Goal> goalList = jpaQueryFactory
                .selectFrom(goal)
                .where(goal.goalFlag.eq(goalFlag))
                .leftJoin(goal.heartList,heart).fetchJoin()
                .fetch();
        List<Heart> heartList = jpaQueryFactory
                .selectFrom(heart)
                .where(heart.goal.goalFlag.eq(goalFlag),heart.socialId.eq(socialId))
                .fetch();
        return GoalsAndHeart.builder().goalList(goalList).heartList(heartList).build();
    }

}
