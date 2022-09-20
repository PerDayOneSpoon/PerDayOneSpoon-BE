package com.sparta.perdayonespoon.domain.follow;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
public class FriendDto {
    private Long id;
    private String nickname;
    private String profileImage;

    private String status;

    @Builder
    @QueryProjection
    public FriendDto(Long id, String nickname, String profileImage,String status){
        this.id = id;
        this.nickname=nickname;
        this.profileImage=profileImage;
        this.status = status;
    }
}
