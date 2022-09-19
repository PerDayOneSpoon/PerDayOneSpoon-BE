package com.sparta.perdayonespoon.domain.dto.response.rate;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.response.rate.QGoalRateDto is a Querydsl Projection type for GoalRateDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QGoalRateDto extends ConstructorExpression<GoalRateDto> {

    private static final long serialVersionUID = -340430288L;

    public QGoalRateDto(com.querydsl.core.types.Expression<String> dayofweek, com.querydsl.core.types.Expression<Boolean> checkGoal, com.querydsl.core.types.Expression<Long> totalcount) {
        super(GoalRateDto.class, new Class<?>[]{String.class, boolean.class, long.class}, dayofweek, checkGoal, totalcount);
    }

}

