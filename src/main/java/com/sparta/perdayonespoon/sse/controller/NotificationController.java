package com.sparta.perdayonespoon.sse.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.sse.dto.NotificationDto;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Api(tags="알림 REST API")
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @GetMapping(value = "/sse/subscribe", consumes = MediaType.ALL_VALUE,produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
                                HttpServletResponse httpServletResponse
                                ) {
        httpServletResponse.addHeader("Content-Type","text/event-stream");
        httpServletResponse.addHeader("X-Accel-Buffering","no");

//        ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM)
//                .cacheControl(CacheControl.noCache())
//                .headers(headers)
//                .body(notificationService.subscribe(principaldetail.getMember().getId(), lastEventId));
//        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM)
//                .cacheControl(CacheControl.noCache())
//                .headers(headers)
//                .body(notificationService.subscribe(principaldetail.getMember().getId(), lastEventId));
        return notificationService.subscribe(principaldetail.getMember(), lastEventId);
    }

    @ApiOperation(value = "알림 메세지 전체 조회", notes = "알림 메세지 조회 API")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @GetMapping("/sse/confirm")
    public ResponseEntity<List<NotificationDto>> getAllSse(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return notificationService.getAllSse(principaldetail);
    }

    @ApiOperation(value = "알림 메세지 전체 읽음 처리", notes = "알림 메세지 전체 읽음 처리 API")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @DeleteMapping("/sse/delete-all")
    public ResponseEntity<MsgDto> deleteAllMessage(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return notificationService.deleteAllMessage(principaldetail);
    }

    @ApiOperation(value = "알림 메세지 개별 읽음 처리", notes = "알림 메세지 개별 읽음 처리 API")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @DeleteMapping("/sse/delete-one/{notificationId}")
    public ResponseEntity<MsgDto> deleteOneMessage(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,@PathVariable Long notificationId){
        return notificationService.deleteOneMessage(principaldetail,notificationId);
    }
}
