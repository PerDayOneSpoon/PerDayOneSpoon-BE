package com.sparta.perdayonespoon.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessMsg {

    FIND_GOALS(HttpStatus.OK, Msg.FIND_GOAL),
    CREATE_GOALS(HttpStatus.OK, Msg.CREATE_GOAL),
    RE_GENERATE_TOKEN(HttpStatus.OK,Msg.RE_GENERATE_TOKEN),
    LOGIN_SUCCESS(HttpStatus.OK, Msg.LOGIN_SUCCESS),
    GET_CALENDER(HttpStatus.OK, Msg.GET_CALENDER),
    CHECK_WEEKLY_HABIT(HttpStatus.OK, Msg.CHECK_WEEKLY_HABIT),
    ACHIEVE_GOAL(HttpStatus.OK, Msg.ACHIEVE_GOAL),
    GET_PROFILE(HttpStatus.OK, Msg.GET_PROFILE),
    LOGOUT_SUCCESS(HttpStatus.OK, Msg.LOGOUT_SUCCESS),
    CANCEL_MEMBERSHIP(HttpStatus.OK, Msg.CANCEL_MEMBERSHIP),
    CHANGE_IMAGE(HttpStatus.OK, Msg.CHANGE_IMAGE),
    CHANGE_STATUS(HttpStatus.OK, Msg.CHANGE_STATUS);





    private final HttpStatus httpStatus;
    private final int code;
    private final String msg;



    SuccessMsg(HttpStatus httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.msg = msg;
    }


    public static class Msg{
        public static final String FIND_GOAL = "목표 확인에 성공하셨습니다. 응원합니다";
        public static final String CREATE_GOAL = "목표 생성에 성공하셨습니다. 응원합니다";
        public static final String RE_GENERATE_TOKEN = "토큰 재발급 성공하셨습니다.";
        public static  final  String LOGIN_SUCCESS = "로그인 완료 되셨습니다.";
        public static  final  String LOGOUT_SUCCESS = "로그아웃 완료 되셨습니다.";
        public static  final  String GET_CALENDER = "금일 캘린더 조회에 성공하셨습니다.";
        public static  final  String CHECK_WEEKLY_HABIT = "주간 습관 확인에 성공하셨습니다. 힘내세요!";
        public static  final  String ACHIEVE_GOAL = "목표 습관 달성 축하드립니다!!! 고생 많으셨어요";
        public static  final  String GET_PROFILE = "프로필 조회에 성공하셨습니다.";
        public static  final  String CANCEL_MEMBERSHIP = "회원탈퇴가 완료 되었습니다.";
        public static  final  String CHANGE_IMAGE = "이미지가 변경 되었습니다.";
        public static  final  String CHANGE_STATUS = "상태가 변경 되었습니다.";

    }
}
