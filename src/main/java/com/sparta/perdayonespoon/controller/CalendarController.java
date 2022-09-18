package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
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
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class CalendarController {
    private final CalendarService calendarService;
    @ApiOperation(value = "캘린더 데이터 조회 API", notes = "캘린더 페이지에서 전체 정보를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/calendar")
    public ResponseEntity getAllDate(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return calendarService.getAlldate(principaldetail);
    }

    @ApiOperation(value = "캘린더 선택 요일 습관 API", notes = "캘린더 페이지에서 선택한 요일의 습관 리스트를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/calendar/{calenderDate}")
    public ResponseEntity getSpecificDate(@PathVariable String calenderDate , @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return calendarService.getSpecipicdate(calenderDate,principaldetail);
    }

    @ApiOperation(value = "친구 캘린더 데이터 조회 API", notes = "캘린더 페이지에서 친구의 전체 정보를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/calendar/friend/{friendId}")
    public ResponseEntity getFriendCalendar(@PathVariable(required = false) Long friendId){
        return calendarService.getFriendCalendar(friendId);
    }

    @ApiOperation(value = "친구 캘린더 데이터 조회 API", notes = "캘린더 페이지에서 친구의 전체 정보를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/calendar/friend/{friendId}/detail")
    public ResponseEntity getFriendSpecific(@PathVariable Long friendId,@RequestParam(value = "specificDate") String specificDate){
        return calendarService.getFriendSpecific(friendId,specificDate);
    }
}
