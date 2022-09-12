package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.domain.dto.response.QMemberSearchDto;

import javax.persistence.EntityManager;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QMember.member;
import static org.springframework.util.ObjectUtils.isEmpty;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<MemberSearchDto> getMember(MemberSearchCondition condition){
        return queryFactory.select(new QMemberSearchDto(member.nickName,member.status,member.image.imgUrl,member.socialCode,member.email))
                .from(member)
                .where(memberEmailEq(condition.getThreeToOne()).or(memberCodeEq(condition.getThreeToOne())).or(memberNickEq(condition.getThreeToOne())))
                .fetch();
    }

    private BooleanExpression memberNickEq(String MemberNick) {
        return isEmpty(MemberNick) ? null : member.nickName.contains(MemberNick);
    }
    private BooleanExpression memberCodeEq(String MemberCode) {
        return isEmpty(MemberCode) ? null : member.socialCode.contains(MemberCode);
    }
    private BooleanExpression memberEmailEq(String memberEmail) {
        return isEmpty(memberEmail) ? null : member.email.contains(memberEmail);
    }
}
