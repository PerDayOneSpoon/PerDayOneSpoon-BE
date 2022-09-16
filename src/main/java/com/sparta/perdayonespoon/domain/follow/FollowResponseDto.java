package com.sparta.perdayonespoon.domain.follow;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class FollowResponseDto {

    private List<FriendDto> friendDtoList;

    private long code;

    private String msg;

    @Builder
    public FollowResponseDto(List<FriendDto> friendDtoList, MsgDto msgDto){
        this.friendDtoList=friendDtoList;
        code = msgDto.getCode();
        msg = msgDto.getMsg();
    }
}

