package com.sparta.perdayonespoon.websocket.dto.request;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import com.sparta.perdayonespoon.websocket.dto.MessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel(value = "메세지 객체", description = "메세지 전송을 위한 객체")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessageRequestDto {

    @ApiModelProperty(value = "메세지 타입", example = "ENTER/TALK/QUIT")
    private MessageType type;

    @ApiModelProperty(value = "채팅방 id")
    private String roomId;

    @ApiModelProperty(value = "보내는 사람 userId")
    private String sender;

    @ApiModelProperty(value = "메세지 내용")
    private String message;

    @ApiModelProperty(value = "메세지 작성자 여부")
    private boolean isMe;

    public ChatMessageRequestDto (ChatRoom room, Member member) {
        this.type = MessageType.ENTER;
        this.roomId = room.getRoomId();
        this.sender = "[알림]";
        this.message = member.getNickname() + "님이 채팅방에 입장 하셨습니다.";
    }

}