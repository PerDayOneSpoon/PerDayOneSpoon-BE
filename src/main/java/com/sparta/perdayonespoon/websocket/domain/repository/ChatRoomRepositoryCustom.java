package com.sparta.perdayonespoon.websocket.domain.repository;

import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepositoryCustom {

    Optional<ChatRoom> getRoom(String roomId);
}
