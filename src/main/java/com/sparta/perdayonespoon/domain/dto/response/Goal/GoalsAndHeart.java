package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Heart;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoalsAndHeart {

    private Goal goal;

    private Heart heart;

    @QueryProjection
    public GoalsAndHeart(Goal goal, Heart heart){
        this.goal = goal;
        this.heart = heart;
    }
}
