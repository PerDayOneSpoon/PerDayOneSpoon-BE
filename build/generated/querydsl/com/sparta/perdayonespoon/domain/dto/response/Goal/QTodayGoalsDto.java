package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.response.Goal.QTodayGoalsDto is a Querydsl Projection type for TodayGoalsDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QTodayGoalsDto extends ConstructorExpression<TodayGoalsDto> {

    private static final long serialVersionUID = 1219707991L;

    public QTodayGoalsDto(com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<java.time.LocalDateTime> startDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> endDate, com.querydsl.core.types.Expression<String> time, com.querydsl.core.types.Expression<Integer> characterUrl, com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<Boolean> privateCheck, com.querydsl.core.types.Expression<java.time.LocalDateTime> currentDate, com.querydsl.core.types.Expression<Boolean> achievementCheck) {
        super(TodayGoalsDto.class, new Class<?>[]{String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, String.class, int.class, long.class, boolean.class, java.time.LocalDateTime.class, boolean.class}, title, startDate, endDate, time, characterUrl, id, privateCheck, currentDate, achievementCheck);
    }

}

