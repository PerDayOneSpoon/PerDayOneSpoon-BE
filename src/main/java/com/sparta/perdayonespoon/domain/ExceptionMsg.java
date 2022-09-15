package com.sparta.perdayonespoon.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMsg {


    // HttpStatus.BAD_REQUEST = 400
    SET_NAME(HttpStatus.BAD_REQUEST, "제목을 입력해 주세요."),
    CHOOSE_CHARACTER(HttpStatus.BAD_REQUEST, "캐릭터를 선택해 주세요."),
    MAX_AMOUNT_OF_GOALS(HttpStatus.BAD_REQUEST, "하루에 최대 5개까지만 습관 생성이 가능합니다."),
    INCORRECT_TIMER(HttpStatus.BAD_REQUEST, "설정한 습관의 타이머를 유효한 값으로 수정해주세요"),
    INCORRECT_GOAL(HttpStatus.BAD_REQUEST, "금일을 넘는 목표는 생성할 수 없습니다. 다시 생성해 주세요"),
    ACHIEVED_OR_NOT(HttpStatus.BAD_REQUEST, "통신시 달성여부가 보내져야 합니다."),
    NOT_EXISTED_HABIT(HttpStatus.BAD_REQUEST, "해당 습관이 존재하지 않습니다."),
    FAIL_TO_CONVERT_FILE(HttpStatus.BAD_REQUEST, "MultipartFile -> 파일 변환 실패"),
    ALREADY_LOGGED_OUT(HttpStatus.BAD_REQUEST, "이미 로그아웃한 사용자입니다."),
    NOT_MATCHED_USER_INFO(HttpStatus.BAD_REQUEST, "유저정보가 일치하지 않습니다."),
    INCORRECT_FORM(HttpStatus.BAD_REQUEST, "형식을 맞춰주세요."),
    DO_IT_PROPERLY(HttpStatus.BAD_REQUEST, "제대로 해주세요."),
    ALREADY_CANCELED_MEMBERSHIP(HttpStatus.BAD_REQUEST, "이미 탈퇴한 회원입니다."),
    NO_IMAGE_FILE(HttpStatus.BAD_REQUEST, "게시글 작성시 이미지 파일이 필요합니다."),
    NO_CONTENTS(HttpStatus.BAD_REQUEST, "입력을 받지 못했습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "리프레쉬 토큰이 유효하지 않습니다.");









    private final String msg;
    private final HttpStatus httpStatus;

    private final int code;

    ExceptionMsg(HttpStatus httpStatus, String msg) {
        this.msg = msg;
        this.code = httpStatus.value();
        this.httpStatus = httpStatus;
    }

    }
