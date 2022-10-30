package com.sparta.perdayonespoon.websocket.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import com.sparta.perdayonespoon.websocket.domain.entity.Participant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatRoomResponseDto implements Comparable<ChatRoomResponseDto> {
    private String roomId;
    private String name;
    private List<ParticipantResponseDto> participantList = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd일 HH-mm-ss초", timezone = "Asia/Seoul")
    private LocalDateTime lastMessage;

    @Builder
    public ChatRoomResponseDto(ChatRoom room, Member member) {
        this.roomId = room.getRoomId();
        this.name = room.getName();
        for (Participant p : room.getParticipantList()) {
            // Front 에서 채팅방 사진 설정을 위해 자기 자신의 정보는 Response 에서 제외 요청
//            if (p.getMember() == member) {
//                continue;
//            }
            this.participantList.add(ParticipantResponseDto.builder().participant(p).build());
        }
        this.lastMessage = room.getLastMessage();
    }

    @Override
    public int compareTo(ChatRoomResponseDto dto) {
        if (lastMessage == null || dto.getLastMessage() == null) {
            return 0;
        }
        return dto.getLastMessage().compareTo(lastMessage);
    }
}