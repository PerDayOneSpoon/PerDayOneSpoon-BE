package com.sparta.perdayonespoon.sse.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags="알림 REST API")
@RestController
@RequiredArgsConstructor
@CrossOrigin(allowedHeaders = "*",exposedHeaders = "*",originPatterns = "*",methods = RequestMethod.GET,origins = "https://www.perday-onespoon.com")
public class NotificationController {
    private final NotificationService notificationService;

    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @GetMapping(value = "sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setCacheControl("no-cache");
//        headers.setConnection("keep-alive");
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

    @ApiOperation(value = "알림 메세지 조회", notes = "알림 메세지 조회 API")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @GetMapping("sse/confirm")
    public ResponseEntity getAllSse(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return notificationService.getAllSse(principaldetail);
    }

}
