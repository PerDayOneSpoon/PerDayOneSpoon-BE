package com.sparta.perdayonespoon.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.follow.FriendDto;
import com.sparta.perdayonespoon.domain.follow.QFriendDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QFriend.friend;
import static com.sparta.perdayonespoon.domain.QMember.member;

@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<FriendDto> getFollowerList(String socialId){
        return jpaQueryFactory
                .select(new QFriendDto(member.id,member.nickname,member.image.imgUrl,member.status))
                .from(friend)
                .where(friend.followingId.eq(socialId))
                .rightJoin(member).on(friend.followerId.eq(member.socialId))
                .fetch();
    }
    @Override
    public List<FriendDto> getFollowingList(String socialId){
        return jpaQueryFactory
                .select(new QFriendDto(member.id,member.nickname,member.image.imgUrl,member.status))
                .from(friend)
                .where(friend.followerId.eq(socialId))
                .rightJoin(member).on(friend.followingId.eq(member.socialId))
                .fetch();
    }
}
