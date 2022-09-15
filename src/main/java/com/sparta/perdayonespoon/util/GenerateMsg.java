package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
//메소드들 서비스
@Getter
@NoArgsConstructor
public class GenerateMsg {
    public static MsgDto getMsg(long code , String msg){
        return MsgDto.builder()
                .code(code)
                .msg(msg)
                .build();
    }
}
