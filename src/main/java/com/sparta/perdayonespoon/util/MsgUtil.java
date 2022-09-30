package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import org.springframework.stereotype.Component;

//메소드들 서비스
@Component
public class MsgUtil {
    public MsgDto getMsg(long code , String msg){
        return MsgDto.builder()
                .code(code)
                .msg(msg)
                .build();
    }
}
