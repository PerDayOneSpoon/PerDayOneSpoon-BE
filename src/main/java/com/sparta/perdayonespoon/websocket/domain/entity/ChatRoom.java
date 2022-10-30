package com.sparta.perdayonespoon.websocket.domain.entity;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.util.Timestamped;
import com.sparta.perdayonespoon.websocket.dto.Type;
import com.sparta.perdayonespoon.websocket.dto.request.ChatRoomPrivateRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chatRoom")
public class ChatRoom extends Timestamped {

    @Id
    private String roomId;

    @Column
    private String name;

    @Column
    private LocalDateTime lastMessage;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    private List<Participant> participantList = new ArrayList<>();

    @Builder(builderClassName = "privateBuilder", builderMethodName = "privateBuilder")
    public ChatRoom(Member member) {
        this.roomId = UUID.randomUUID().toString();
        this.name = member.getNickname();
        this.type = Type.PRIVATE;
        this.lastMessage = LocalDateTime.now();
    }

    public void addParticipant(Participant participant) {
        this.participantList.add(participant);
    }

    @Builder(builderClassName = "publicBuilder", builderMethodName = "publicBuilder")
    public ChatRoom(String name) {
        this.roomId = UUID.randomUUID().toString();
        this.name = name;
        this.type = Type.PUBLIC;
        this.lastMessage = LocalDateTime.now();
    }



    public void newMessage() {
        this.lastMessage = LocalDateTime.now();
    }
}