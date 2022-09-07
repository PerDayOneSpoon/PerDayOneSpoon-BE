package com.sparta.perdayonespoon.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode { //이렇게 해주는 방법도 있다. 현재 코드에는 적용되지않았다.

    //200 Response to 400 Bad Request
    NEED_A_LOGIN(HttpStatus.OK, "로그인이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    //    private final String errorCode;
    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}

