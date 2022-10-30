package com.sparta.perdayonespoon.websocket.domain.repository;

import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String>, ChatRoomRepositoryCustom {
    Optional<ChatRoom> findById(String id);
}
