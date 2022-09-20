package com.sparta.perdayonespoon.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessMsg {

    FIND_GOALS(HttpStatus.OK, "목표 확인에 성공하셨습니다. 응원합니다"),
    CREATE_GOALS(HttpStatus.OK, "목표 생성에 성공하셨습니다. 응원합니다"),
    RE_GENERATE_TOKEN(HttpStatus.OK,"토큰 재발급 성공하셨습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 완료 되셨습니다."),
    GET_CALENDER(HttpStatus.OK, "금일 캘린더 조회에 성공하셨습니다."),
    CHECK_WEEKLY_HABIT(HttpStatus.OK, "주간 습관 확인에 성공하셨습니다. 힘내세요!"),
    ACHIEVE_GOAL(HttpStatus.OK, "목표 습관 달성 축하드립니다!!! 고생 많으셨어요"),
    GET_PROFILE(HttpStatus.OK, "프로필 조회에 성공하셨습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 완료 되셨습니다."),
    CANCEL_MEMBERSHIP(HttpStatus.OK, "회원탈퇴가 완료 되었습니다."),
    CHANGE_IMAGE(HttpStatus.OK, "이미지가 변경 되었습니다."),
    CHANGE_STATUS(HttpStatus.OK, "상태가 변경 되었습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String msg;

    SuccessMsg(HttpStatus httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.msg = msg;
    }

}
