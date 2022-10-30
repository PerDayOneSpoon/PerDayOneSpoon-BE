package com.sparta.perdayonespoon.websocket.domain.entity;

import com.sparta.perdayonespoon.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn
    private Member member;

    @Builder
    public Participant(Member member, ChatRoom room) {
        this.member = member;
        this.member.getParticipantList().add(this);
        this.chatRoom = room;
        this.chatRoom.getParticipantList().add(this);
    }

}
