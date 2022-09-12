package com.sparta.perdayonespoon.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.QTwoFieldDto;
import com.sparta.perdayonespoon.domain.dto.response.TwoFieldDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;


import java.util.List;

import static com.sparta.perdayonespoon.domain.QMember.member;
import static com.sparta.perdayonespoon.domain.QRefreshToken.refreshToken;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public TwoFieldDto getMember(TokenSearchCondition condition) {
//        return queryFactory.select(member).from(member,refreshToken)
//                .rightJoin(refreshToken).on(member.socialId.eq(refreshToken.key))
//                .where(member.socialId.eq(refreshToken.key),refreshtokenEq(condition.getRefreshtoken()))
//                .fetchOne();
        return queryFactory.select(new QTwoFieldDto(member,refreshToken)).from(member)
                .rightJoin(refreshToken).on(member.socialId.eq(refreshToken.key))
                .where(refreshTokenEq(condition.getRefreshtoken()))
                .fetchOne();
    }

    private BooleanExpression refreshTokenEq(String refreshtoken) {
        return isEmpty(refreshtoken) ? null : refreshToken.value.eq(refreshtoken);
    }
}
