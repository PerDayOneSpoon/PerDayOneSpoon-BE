package com.sparta.perdayonespoon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.Badge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QBadge.badge;
import static com.sparta.perdayonespoon.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class BadgeRepositoryImpl implements BadgeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Badge> findAllByMember_Id(Long id){
        return queryFactory
                .selectFrom(badge)
                .join(badge.member, member)
                .where(member.id.eq(id))
                .orderBy(badge.badgeNumber.asc())
                .fetch();
    }
}
