package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.Generated;

/**
 * com.sparta.perdayonespoon.domain.dto.response.QMemberSearchDto is a Querydsl Projection type for MemberSearchDto
 */
@Generated("com.querydsl.codegen.ProjectionSerializer")
public class QMemberSearchDto extends ConstructorExpression<MemberSearchDto> {

    private static final long serialVersionUID = 2101953323L;

    public QMemberSearchDto(com.querydsl.core.types.Expression<String> nickname, com.querydsl.core.types.Expression<String> status, com.querydsl.core.types.Expression<String> ImgUrl, com.querydsl.core.types.Expression<String> socialCode, com.querydsl.core.types.Expression<String> Email) {
        super(MemberSearchDto.class, new Class<?>[]{String.class, String.class, String.class, String.class, String.class}, nickname, status, ImgUrl, socialCode, Email);
    }

}

