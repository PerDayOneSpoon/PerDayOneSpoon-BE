package com.sparta.perdayonespoon.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.QCountDto is a Querydsl Projection type for CountDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QCountDto extends ConstructorExpression<CountDto> {

    private static final long serialVersionUID = -2072782389L;

    public QCountDto(com.querydsl.core.types.Expression<String> currentDate, com.querydsl.core.types.Expression<Long> totalCount) {
        super(CountDto.class, new Class<?>[]{String.class, long.class}, currentDate, totalCount);
    }

}

