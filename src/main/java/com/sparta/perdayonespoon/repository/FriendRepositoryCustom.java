package com.sparta.perdayonespoon.repository;

import com.sparta.perdayonespoon.domain.follow.FriendDto;

import java.util.List;

public interface FriendRepositoryCustom {

    List<FriendDto> getFollowerList(String socialId);

    List<FriendDto> getFollowingList(String socialId);
}
