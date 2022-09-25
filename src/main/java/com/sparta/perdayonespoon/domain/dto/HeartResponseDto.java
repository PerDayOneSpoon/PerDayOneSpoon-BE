package com.sparta.perdayonespoon.domain.dto;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Builder;
import lombok.Data;

@Data
public class HeartResponseDto {
    private boolean heartCheck;
    private long code;
    private String msg;

    private long heartCnt;

    @Builder
    public HeartResponseDto(boolean heartCheck, MsgDto msgDto, long heartCnt){
        this.heartCheck = heartCheck;
        this.code= msgDto.getCode();
        this.msg = msgDto.getMsg();
        this.heartCnt = heartCnt;
    }
}
