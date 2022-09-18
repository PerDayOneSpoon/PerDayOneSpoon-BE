package com.sparta.perdayonespoon.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class RestApiException {
    private String errorMessage;
    private boolean resultFlag;
    private long code;

    @Builder
    public RestApiException(String errorMessage, boolean resultFlag, long code){
        this.errorMessage =errorMessage;
        this.resultFlag =resultFlag;
        this.code =code;
    }
}