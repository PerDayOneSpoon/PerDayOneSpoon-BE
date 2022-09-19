package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.response.QTwoFieldDto is a Querydsl Projection type for TwoFieldDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QTwoFieldDto extends ConstructorExpression<TwoFieldDto> {

    private static final long serialVersionUID = 1023778175L;

    public QTwoFieldDto(com.querydsl.core.types.Expression<? extends com.sparta.perdayonespoon.domain.Member> member, com.querydsl.core.types.Expression<? extends com.sparta.perdayonespoon.domain.RefreshToken> refreshToken) {
        super(TwoFieldDto.class, new Class<?>[]{com.sparta.perdayonespoon.domain.Member.class, com.sparta.perdayonespoon.domain.RefreshToken.class}, member, refreshToken);
    }

}

