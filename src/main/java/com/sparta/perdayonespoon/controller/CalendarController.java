package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.CalendarRequestDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.DayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarUniteDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.CalendarService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


@Api(tags="캘린더 페이지 REST API")
@RequestMapping("/confirm")
@RequiredArgsConstructor
@RestController
public class CalendarController {
    private final CalendarService calendarService;
    @ApiOperation(value = "캘린더 데이터 조회 API", notes = "캘린더 페이지에서 전체 정보를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)})})
    @GetMapping("/calendar")
    public ResponseEntity<CalendarUniteDto> getAllDate(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return calendarService.getAlldate(principaldetail);
    }

    @ApiOperation(value = "캘린더 선택한 요일 습관 API", notes = "캘린더 페이지에서 선택한 요일의 습관 리스트를 보여주는 api ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
            @ApiImplicitParam(name = "calendarDate", dataType = "string", paramType = "path", value = "특정 날짜 - yyyy-MM-dd"),
            @ApiImplicitParam(name = "memberId", dataType = "Long", paramType = "query", value = "사람 Id", example = "0")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)})})
    @GetMapping("/calendar/member/{calendarDate}")
    public ResponseEntity<DayGoalsDto> findMemberSpecificDate(@ModelAttribute CalendarRequestDto calendarRequestDto, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return calendarService.findMemberSpecificDate(calendarRequestDto,principaldetail);
    }

    @ApiOperation(value = "친구 캘린더 데이터 조회 API", notes = "캘린더 페이지에서 친구의 전체 정보를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)})})
    @GetMapping("/calendar/friend/{friendId}")
    public ResponseEntity getFriendCalendar(@PathVariable(required = false) Long friendId , @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return calendarService.getFriendCalendar(friendId,principaldetail);
    }

    @ApiOperation(value = "캘린더 선택한 달의 습관 API", notes = "캘린더 페이지에서 선택한 달의 습관 리스트를 보여주는 api ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
            @ApiImplicitParam(name = "calendarYearAndMonth", dataType = "string", paramType = "path", value = "특정 월 - 2022-04"),
            @ApiImplicitParam(name = "memberId", dataType = "Long", paramType = "query", value = "사람 Id", example = "0")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)})})
    @GetMapping("/calendar/month/{calendarYearAndMonth}")
    public ResponseEntity findMemberSpecificMonth(@ModelAttribute CalendarRequestDto calendarRequestDto, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return calendarService.findMemberSpecificMonth(calendarRequestDto,principaldetail);
    }
}
