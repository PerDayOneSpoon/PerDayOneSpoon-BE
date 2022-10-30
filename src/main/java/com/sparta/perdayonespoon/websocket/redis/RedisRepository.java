package com.sparta.perdayonespoon.websocket.redis;

import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import com.sparta.perdayonespoon.websocket.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisRepository {

    private final RedisSub redisSub;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final ChatRoomRepository chatRoomRepository;
    private Map<String, ChannelTopic> topicMap;

    // 임시로 설정
    @PostConstruct
    private void init() {
        topicMap = new HashMap<>();
        List<ChatRoom> roomList = chatRoomRepository.findAll();
        for (ChatRoom c : roomList) {
            ChannelTopic topic = ChannelTopic.of(c.getRoomId());
            redisMessageListenerContainer.addMessageListener(redisSub, topic);
            topicMap.put(c.getRoomId(), topic);
        }
    }

    // 채팅방 생성 시 사용해야함
    public void subscribe(String roomId) {
        ChannelTopic topic = topicMap.get(roomId);
        if (topic == null) {
            topic = ChannelTopic.of(roomId);
            redisMessageListenerContainer.addMessageListener(redisSub, topic);
            topicMap.put(roomId, topic);
        }
    }

    public ChannelTopic getTopic(String roomId) {
        return topicMap.get(roomId);
    }

}