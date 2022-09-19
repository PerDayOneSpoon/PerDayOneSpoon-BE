package com.sparta.perdayonespoon.domain.dto.response.calender;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.response.calender.QCalenderGoalsDto is a Querydsl Projection type for CalenderGoalsDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QCalenderGoalsDto extends ConstructorExpression<CalenderGoalsDto> {

    private static final long serialVersionUID = 1444212699L;

    public QCalenderGoalsDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<java.time.LocalDateTime> startDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> endDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> currentDate, com.querydsl.core.types.Expression<String> time, com.querydsl.core.types.Expression<Integer> characterId, com.querydsl.core.types.Expression<Boolean> privateCheck, com.querydsl.core.types.Expression<Boolean> achievementCheck) {
        super(CalenderGoalsDto.class, new Class<?>[]{long.class, String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, String.class, int.class, boolean.class, boolean.class}, id, title, startDate, endDate, currentDate, time, characterId, privateCheck, achievementCheck);
    }

}

