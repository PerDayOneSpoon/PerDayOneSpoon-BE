package com.sparta.perdayonespoon.websocket.domain.repository;

import com.sparta.perdayonespoon.websocket.domain.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepositoryCustom {

    List<ChatMessage> findAllMessageById(String roomId);
}
