package com.sparta.perdayonespoon.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.QTwoFieldDto;
import com.sparta.perdayonespoon.domain.dto.response.TwoFieldDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;

import javax.persistence.EntityManager;


import java.util.List;

import static com.sparta.perdayonespoon.domain.QMember.member;
import static com.sparta.perdayonespoon.domain.QRefreshToken.refreshToken;
import static org.springframework.util.ObjectUtils.isEmpty;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public RefreshTokenRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // refreshtoken repo -> represhtoken을 찾아옴1번
    // refreshtoken.getkey() -> memberRepo -> member찾기 2번 db접근 2번
    // 두개의 엔티티는 연관x join innerjoin -> 연관되야 걸리니까?
    // leftjoin을쓴거다 외부조인인 연관이 안걸려있으니까 뭐하냐? 바아로~? on 절을걸어어버렸다?
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
