package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.config.QuerydslConfig;
import com.sparta.perdayonespoon.domain.Heart;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalsAndHeart;
import com.sparta.perdayonespoon.domain.dto.response.Goal.QGoalsAndHeart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static com.sparta.perdayonespoon.domain.QHeart.heart;

@Repository
@RequiredArgsConstructor
public class HeartRepositoryImpl implements HeartRepositoryCustom{
    private final QuerydslConfig querydslConfig;

    @Override
    public List<GoalsAndHeart> findGoalsHeart(String goalFlag){
        return querydslConfig.jpaQueryFactory()
                .select(new QGoalsAndHeart(goal,heart))
                .from(heart)
                .innerJoin(heart.goal,goal)
                .where(goal.goalFlag.eq(goalFlag))
                .fetch();
    }
}
