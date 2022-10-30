package com.sparta.perdayonespoon.websocket.controller;

import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.websocket.dto.MessageType;
import com.sparta.perdayonespoon.websocket.dto.request.ChatMessageRequestDto;
import com.sparta.perdayonespoon.websocket.dto.response.ChatMessageResponseDto;
import com.sparta.perdayonespoon.websocket.service.ChatMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags="웹소켓 채팅 REST API ")
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @ApiOperation(value = "메세지 전송(/pub)")
    @MessageMapping("/chat/enter/{roomId}")
    public void enterMessage(@PathVariable String roomId, @AuthenticationPrincipal Principaldetail principaldetail) {
        chatMessageService.sendEnterMessage1(roomId, principaldetail.getMember());
    }

    @ApiOperation(value = "메세지 전송(/pub)")
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequestDto message) {
        message.setType(MessageType.TALK);
        message.setSender("배지");
        log.info("log.info(\"오냐?\");");
        chatMessageService.sendChatMessage(message);
    }

    // 페이징 처리해야해서 나중에 쿼리 파라미터로 size, page 값을 받으므로 roomId 도 똑같은 형태로 받은 것!!
//    @ResponseBody
//    @GetMapping("/chat/message/before")
//    public ResponseEntity<Page<ChatMessageResponseDto>> getSavedMessages(
//            @RequestParam String roomId
//    ) {
//        LoadUser.loginAndNickCheck();
//        System.out.println("메세지");
//        Page<ChatMessageResponseDto> responseDtoList = chatMessageService.getSavedMessages(roomId);
//        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }