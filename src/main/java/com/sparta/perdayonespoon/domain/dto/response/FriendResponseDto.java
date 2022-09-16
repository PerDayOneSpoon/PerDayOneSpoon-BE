package com.sparta.perdayonespoon.domain.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class FriendResponseDto {

    private boolean followCheck;

    private long code;

    private String msg;

    @Builder
    public FriendResponseDto ( MsgDto msgDto , boolean followCheck){
        this.code= msgDto.getCode();
        this.msg= msgDto.getMsg();
        this.followCheck = followCheck;
    }
}
