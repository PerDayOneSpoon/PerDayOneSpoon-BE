package com.sparta.perdayonespoon.websocket.domain.repository;

import com.sparta.perdayonespoon.websocket.domain.entity.ChatMessage;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>,ChatMessageRepositoryCustom {
    List<ChatMessage> findAllByRoomIdOrderByCreatedAtAsc(String roomId);
//    Page<ChatMessage> findAllByRoomId(Pageable pageable, String roomId);
}
