package com.sparta.perdayonespoon.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MsgCollector {

    FIND_GOALS(HttpStatus.OK, Msg.FIND_GOAL),
    CREATE_GOALS(HttpStatus.OK, Msg.CREATE_GOAL),

    RE_GENERATE_TOKEN(HttpStatus.OK,Msg.RE_GENERATE_TOKEN);

    private final HttpStatus httpStatus;
    private final int code;
    private final String msg;

    MsgCollector(HttpStatus httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.msg = msg;
    }
    public static class Msg{
        public static final String FIND_GOAL = "목표 확인에 성공하셨습니다. 응원합니다";
        public static final String CREATE_GOAL = "목표 생성에 성공하셨습니다. 응원합니다";

        public static final String RE_GENERATE_TOKEN = "토큰 재발급 성공하셨습니다.";
    }
}
