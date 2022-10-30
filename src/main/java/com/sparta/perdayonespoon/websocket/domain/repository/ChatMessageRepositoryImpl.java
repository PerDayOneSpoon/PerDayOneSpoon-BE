package com.sparta.perdayonespoon.websocket.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.perdayonespoon.domain.QMember.member;
import static com.sparta.perdayonespoon.websocket.domain.entity.QChatMessage.chatMessage;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public List<ChatMessage> findAllMessageById(String roomId){
        return queryFactory
                .selectFrom(chatMessage)
                .where(chatMessage.roomId.eq(roomId))
                .leftJoin(chatMessage.member, member).fetchJoin()
                .orderBy(chatMessage.createdAt.asc())
                .fetch();
    }
}
