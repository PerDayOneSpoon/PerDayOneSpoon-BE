package com.sparta.perdayonespoon.websocket.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.domain.QMember;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import com.sparta.perdayonespoon.websocket.domain.entity.QChatRoom;
import com.sparta.perdayonespoon.websocket.domain.entity.QParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sparta.perdayonespoon.websocket.domain.entity.QChatRoom.chatRoom;
import static com.sparta.perdayonespoon.websocket.domain.entity.QParticipant.participant;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public Optional<ChatRoom> getRoom(String roomId){
        return Optional.ofNullable(queryFactory
                .selectFrom(chatRoom)
                .innerJoin(chatRoom.participantList, participant).fetchJoin()
                .where(chatRoom.roomId.eq(roomId))
                .fetchOne());
    }
}
