package com.sparta.perdayonespoon.websocket.redis;

import com.sparta.perdayonespoon.websocket.dto.request.ChatMessageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPub {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, ChatMessageRequestDto message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

}