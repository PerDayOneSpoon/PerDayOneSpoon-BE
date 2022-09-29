package com.sparta.perdayonespoon.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendBadgeCheckDto {

    private boolean isFollower;

    private boolean isFollowing;

    @QueryProjection
    public FriendBadgeCheckDto(boolean isFollower, boolean isFollowing){
        this.isFollower=isFollower;
        this.isFollowing=isFollowing;
    }
}
