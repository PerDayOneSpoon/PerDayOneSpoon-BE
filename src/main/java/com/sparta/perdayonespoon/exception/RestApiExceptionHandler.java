package com.sparta.perdayonespoon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.util.StringUtils;

@Slf4j
@RestControllerAdvice // @ControllerAdvice + @RequestBody
public class RestApiExceptionHandler {


    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(final CustomException e) {
        log.error("handleCustomException: {}", e.getErrorCode());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus().value())
                .body(new ErrorResponse(e.getErrorCode()));
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<RestApiException> handleApiRequestException(IllegalArgumentException ex) {
        String message = getExceptionMessage(ex.getMessage());
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        log.error(message,stackTraceElements[0]);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RestApiException.builder()
                .resultFlag(false)
                .code(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .build());}

    private String getExceptionMessage(String message){
        if(StringUtils.hasText(message)){
            return message + "\n \t {}";
        }
        return "\n \t {}";
    }
}
