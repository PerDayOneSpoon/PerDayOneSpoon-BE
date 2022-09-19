package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.response.Goal.QGoalResponseDto is a Querydsl Projection type for GoalResponseDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QGoalResponseDto extends ConstructorExpression<GoalResponseDto> {

    private static final long serialVersionUID = -743902462L;

    public QGoalResponseDto(com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> startDate, com.querydsl.core.types.Expression<String> endDate, com.querydsl.core.types.Expression<String> time, com.querydsl.core.types.Expression<Long> characterId, com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<Boolean> privateCheck, com.querydsl.core.types.Expression<String> socialId, com.querydsl.core.types.Expression<String> currentdate, com.querydsl.core.types.Expression<Boolean> achievementCheck) {
        super(GoalResponseDto.class, new Class<?>[]{String.class, String.class, String.class, String.class, long.class, long.class, boolean.class, String.class, String.class, boolean.class}, title, startDate, endDate, time, characterId, id, privateCheck, socialId, currentdate, achievementCheck);
    }

}

