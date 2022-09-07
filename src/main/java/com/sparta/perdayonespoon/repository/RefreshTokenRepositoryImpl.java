package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;

import javax.persistence.EntityManager;

import static com.sparta.perdayonespoon.domain.QRefreshToken.refreshToken;
import static org.springframework.util.ObjectUtils.isEmpty;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RefreshTokenRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Member getMember(TokenSearchCondition condition){
        return Member.builder().build();
    }


    private BooleanExpression userNicEq(String userNic) {
        return isEmpty(userNic) ? null : refreshToken.value.contains(userNic);
    }
}
