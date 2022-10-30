package com.sparta.perdayonespoon.websocket.controller;

import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.websocket.dto.MsgEnum;
import com.sparta.perdayonespoon.websocket.dto.request.ChatRoomExitRequestDto;
import com.sparta.perdayonespoon.websocket.dto.request.ChatRoomPrivateRequestDto;
import com.sparta.perdayonespoon.websocket.dto.response.ChatMessageResponseDto;
import com.sparta.perdayonespoon.websocket.dto.response.ChatRoomResponseDto;
import com.sparta.perdayonespoon.websocket.service.ChatRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Api(tags="채팅방 REST API ")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "access_token")
    @ApiOperation(value = "일대일 채팅방 생성")
    @PostMapping("/room/private/{friendId}")
    public ResponseEntity<ChatRoomResponseDto> createPrivateRoom(@PathVariable Long friendId, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        ChatRoomResponseDto responseDto = chatRoomService.createPrivateRoom(friendId, principaldetail);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "access_token")
    @ApiOperation(value = "채팅방 목록 조회")
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponseDto>> getRoomList(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        List<ChatRoomResponseDto> responseDtoList = chatRoomService.findAllRoom(principaldetail);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "access_token"),
            @ApiImplicitParam(name = "roomId", dataType = "string", paramType = "path", value = "방 번호"),
    })
    @ApiOperation(value = "이전메세지 조회 Rest Api")
    @GetMapping("/room/message/{roomId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getRoom(@PathVariable String roomId, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        return chatRoomService.findById(roomId, principaldetail);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "access_token"),
            @ApiImplicitParam(name = "roomId", dataType = "string", paramType = "path", value = "방 번호"),
    })
    @ApiOperation(value = "채팅방 입장(유저 정보) Rest Api")
    @GetMapping("/room/userInfo/{roomId}")
    public ResponseEntity getUserInfo(@PathVariable String roomId, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        return chatRoomService.getUserInfo(roomId, principaldetail);
    }

    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "access_token")
    @ApiOperation(value = "채팅방 나가기")
    @DeleteMapping("/room")
    public ResponseEntity<String> exitRoom(@RequestBody ChatRoomExitRequestDto requestDto, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        chatRoomService.exitRoom(requestDto, principaldetail);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(new MediaType("applicaton", "text", StandardCharsets.UTF_8))
                .body(MsgEnum.CHAT_ROOM_EXIT.getMsg());
    }
}