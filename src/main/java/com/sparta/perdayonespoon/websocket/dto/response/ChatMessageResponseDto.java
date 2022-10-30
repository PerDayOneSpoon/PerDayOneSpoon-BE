package com.sparta.perdayonespoon.websocket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatMessage;
import com.sparta.perdayonespoon.websocket.dto.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponseDto {

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;

    private boolean isMe;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    private String profileImageUrl;

    @Builder(builderClassName = "standardBuilder", builderMethodName =  "standardBuilder")
    public ChatMessageResponseDto(ChatMessage message) {
        this.type = message.getType();
        this.roomId = message.getRoomId();
        if (Objects.equals(message.getSender(), "[알림]")) {
            this.sender = message.getSender();
        } else {
            this.sender = message.getMember().getNickname();
        }
        this.message = message.getMessage();
        this.createdDate = message.getCreatedAt();
        if (message.getMember() != null) {
            this.profileImageUrl = message.getMember().getImage().getImgUrl();
        }
    }
    public void isMeCheck(boolean isMe){
        this.isMe = isMe;
    }
}