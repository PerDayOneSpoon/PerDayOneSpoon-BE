package com.sparta.perdayonespoon.websocket.service;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.sse.domain.repository.NotificationRepository;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatMessage;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import com.sparta.perdayonespoon.websocket.domain.entity.Participant;
import com.sparta.perdayonespoon.websocket.domain.repository.ChatMessageRepository;
import com.sparta.perdayonespoon.websocket.domain.repository.ChatRoomRepository;
import com.sparta.perdayonespoon.websocket.dto.MessageType;
import com.sparta.perdayonespoon.websocket.dto.Type;
import com.sparta.perdayonespoon.websocket.dto.request.ChatMessageRequestDto;
import com.sparta.perdayonespoon.websocket.dto.response.ChatMessageResponseDto;
import com.sparta.perdayonespoon.websocket.redis.RedisPub;
import com.sparta.perdayonespoon.websocket.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final RedisPub redisPub;

    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messageSendingOperations;

    @Transactional
    public void sendChatMessage(ChatMessageRequestDto message) {
        log.info("오냐?");
//        Member member = memberRepository.findById(member1.getId()).orElseThrow(
//                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
//        );
        ChatRoom room = chatRoomRepository.findById(message.getRoomId()).orElseThrow(
                () -> new IllegalArgumentException("채팅방을 찾을 수 없습니다.")
        );
        messageSendingOperations.convertAndSend("/sub/chat/room/1" , message.getMessage());
//        if (message.getType() == MessageType.QUIT) {
//            if (room.getType() == Type.PRIVATE) {
//                message.setSender("[알림]");
//                message.setMessage(member.getNickname() + "님이 채팅방을 나가셨습니다. 새로운 채팅방에서 채팅을 진행해 주세요!");
//            } else {
//                message.setSender("[알림]");
//                message.setMessage(member.getNickname() + "님이 채팅방을 나가셨습니다.");
//            }
//        }
        // 중간에 ResponseDto 로 변경하는 부분 필요 -> 지금은 LocalDateTime 직렬화 오류 현상 때문에 생략
//        room.newMessage();
//        chatMessageRepository.save(ChatMessage.defaultBuilder().requestDto(message).build());
////        this.saveChatMessage(message,member);
//        // 자신 제외 알림 전송
//        List<Member> userList = new ArrayList<>();
//        for (Participant p : room.getParticipantList()) {
//            userList.add(p.getMember());
//        }
////        for (Member u : userList) {
////            if (u == member) {
////                continue;
////            }
////            String notification = room.getRoomId();
//////            NotificationRequestDto requestDto = new NotificationRequestDto(com.example.backend.notification.domain.Type.채팅, notification);
//////            notificationRepository.save(new Notification(requestDto, u));
////        }
//        redisPub.publish(redisRepository.getTopic(room.getRoomId()), message);
    }

    @Transactional
    public void sendEnterMessage(ChatRoom room, Member member) {
        ChatMessageRequestDto message = new ChatMessageRequestDto(room, member);
        room.newMessage();
        this.saveChatMessage(message,member);
        redisPub.publish(redisRepository.getTopic(room.getRoomId()), message);
    }

    @Transactional
    public void sendEnterMessage1(String roomId, Member member) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()
                      -> new IllegalArgumentException("방이 없습니다."));
        ChatMessageRequestDto message = new ChatMessageRequestDto(chatRoom, member);
        chatRoom.newMessage();
        this.saveChatMessage(message,member);
        redisPub.publish(redisRepository.getTopic(chatRoom.getRoomId()), message);
    }

    @Transactional
    public void saveChatMessage(ChatMessageRequestDto message,Member member) {
        if(!Objects.equals(message.getSender(), "[알림]")) {
            chatMessageRepository.save(ChatMessage.memberChatBuilder()
                    .requestDto(message)
                    .member(member)
                    .build());
        }else chatMessageRepository.save(ChatMessage.defaultBuilder().requestDto(message).build());

//        if (!Objects.equals(message.getSender(), "[알림]")) {
//            Member member = memberRepository.findByNickname(message.getSender()).orElseThrow(
//                    () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
//            );
//            chatMessageRepository.save(new ChatMessage(message, message.getMember()));
//        } else {
//            chatMessageRepository.save(new ChatMessage(message,message.getMember()));
//        }

    }

//    // 페이징으로 받아서 무한 스크롤 가능할듯
//    public Page<ChatMessageResponseDto> getSavedMessages(String roomId) {
//
//        Pageable pageable = PageRequest.of(0, 100, Sort.by("createdDate").descending());
//        Page<ChatMessage> messagePage = chatMessageRepository.findAllByRoomId(pageable, roomId);
//        return messagePage.map(ChatMessageResponseDto::new);
//
//    }

}
