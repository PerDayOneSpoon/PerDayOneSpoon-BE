package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.QFriend;
import com.sparta.perdayonespoon.domain.QGoal;
import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.domain.dto.response.MyPageCollectDto;
import com.sparta.perdayonespoon.domain.dto.response.QMemberSearchDto;
import com.sparta.perdayonespoon.domain.dto.response.QMyPageCollectDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.perdayonespoon.domain.QFriend.friend;
import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static com.sparta.perdayonespoon.domain.QMember.member;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberSearchDto> getMember(MemberSearchCondition condition,String socialId){
        return queryFactory.select(new QMemberSearchDto(member.nickname,member.status,member.image.imgUrl,member.socialCode,member.email
                        ,member.socialId,member.id,
                        new CaseBuilder()
                                .when(member.socialId.eq(friend.followerId).and(friend.followingId.eq(socialId))).then(true)
                                .when(member.socialId.eq(socialId)).then(false)
                                .otherwise(true)))
                .from(member)
                .where(memberEmailEq(condition.getThreeToOne()).or(memberCodeEq(condition.getThreeToOne())).or(memberNickEq(condition.getThreeToOne())))
                .leftJoin(friend).on(member.socialId.eq(friend.followerId).and(friend.followingId.eq(socialId)))
                .fetch();
    }

    // 추후 SubQuery중 goal은 member와 연관시켜 해결할 예정 -> 성능 테스트 후 변경 사항 적용
    @Override
    public MyPageCollectDto getMypageData(String socialId){
        return queryFactory.select(new QMyPageCollectDto(member,
                        JPAExpressions.select(goal.count()).from(goal).where(goal.socialId.eq(socialId)).groupBy(goal.socialId,goal.achievementCheck).having(goal.achievementCheck.eq(true)),
                        JPAExpressions.select(friend.count()).from(friend).where(friend.followingId.eq(socialId)).groupBy(friend.followingId),
                        JPAExpressions.select(friend.count()).from(friend).where(friend.followerId.eq(socialId)).groupBy(friend.followerId)))
                .from(member)
                .where(member.socialId.eq(socialId))
                .fetchOne();
    }


    private BooleanExpression memberNickEq(String MemberNick) {
        return isEmpty(MemberNick) ? null : member.nickname.contains(MemberNick);
    }
    private BooleanExpression memberCodeEq(String MemberCode) {
        return isEmpty(MemberCode) ? null : member.socialCode.contains(MemberCode);
    }
    private BooleanExpression memberEmailEq(String memberEmail) {
        return isEmpty(memberEmail) ? null : member.email.contains(memberEmail);
    }
}
