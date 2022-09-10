package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
