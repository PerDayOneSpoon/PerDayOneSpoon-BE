package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Member;
import lombok.Data;

import java.util.stream.Collectors;

@Data
public class MyPageCollectDto {
    private String nickname; // 이름

    private String profileImage; // 사진

    private String status; // 상태메세지

    private String socialId; // 소셜id

    private String socialCode; //검색코드

    private String socialEmail; // 이메일

    private long goalCnt; // 달성 목표 개수

    private long followerCnt; // 팔로우한 친구 수 int <

    private long followingCnt; // 팔로워한 친구 수

    private int badgeCnt; // 뱃지 개수

    private long code;

    private String msg;


    @QueryProjection
    public MyPageCollectDto(Member member,long goalCnt,long followerCnt, long followingCnt){
        nickname = member.getNickname();
        profileImage = member.getImage().getImgUrl();
        status = member.getStatus();
        socialId = member.getSocialId();
        socialCode = member.getSocialCode();
        socialEmail = member.getEmail();
        this.goalCnt = goalCnt;
        this.followerCnt = followerCnt;
        this.followingCnt = followingCnt;
        badgeCnt = (int) member.getBadgeList().stream().map(Badge::getBadgeName).distinct().count();
    }

    public void SetCodeMsg(MsgDto msgDto){
        code= msgDto.getCode();
        msg=msgDto.getMsg();
    }

}
