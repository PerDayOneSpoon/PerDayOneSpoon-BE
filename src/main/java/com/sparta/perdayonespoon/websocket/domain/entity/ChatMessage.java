package com.sparta.perdayonespoon.websocket.domain.entity;


import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.util.Timestamped;
import com.sparta.perdayonespoon.websocket.dto.MessageType;
import com.sparta.perdayonespoon.websocket.dto.request.ChatMessageRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column
    private String roomId;

    @Column
    private String sender;

    @Column
    private String message;

    @ManyToOne
    @JoinColumn
    private Member member;

    @Builder(builderClassName = "memberChatBuilder", builderMethodName = "memberChatBuilder")
    public ChatMessage(ChatMessageRequestDto requestDto, Member member) {
        this.type = requestDto.getType();
        this.roomId = requestDto.getRoomId();
        this.sender = member.getNickname();
        this.message = requestDto.getMessage();
        this.member = member;
    }

    @Builder(builderClassName = "defaultBuilder", builderMethodName = "defaultBuilder")
    public ChatMessage(ChatMessageRequestDto requestDto) {
        this.type = requestDto.getType();
        this.roomId = requestDto.getRoomId();
        this.sender = requestDto.getSender();
        this.message = requestDto.getMessage();
    }
}
