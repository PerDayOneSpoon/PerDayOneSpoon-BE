package com.sparta.perdayonespoon.sse.controller;

import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags="알림 REST API")
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @ApiOperation(value = "알림 구독", notes = "알림을 구독한다.")
    @GetMapping(value = "sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId)  {
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
        return notificationService.subscribe(principaldetail.getMember().getId(), lastEventId);
    }
}
