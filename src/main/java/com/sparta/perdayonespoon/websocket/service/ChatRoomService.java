package com.sparta.perdayonespoon.websocket.service;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatMessage;
import com.sparta.perdayonespoon.websocket.domain.entity.ChatRoom;
import com.sparta.perdayonespoon.websocket.domain.entity.Participant;
import com.sparta.perdayonespoon.websocket.domain.repository.ChatMessageRepository;
import com.sparta.perdayonespoon.websocket.domain.repository.ChatRoomRepository;
import com.sparta.perdayonespoon.websocket.domain.repository.ParticipantRepository;
import com.sparta.perdayonespoon.websocket.dto.Type;
import com.sparta.perdayonespoon.websocket.dto.request.ChatRoomExitRequestDto;
import com.sparta.perdayonespoon.websocket.dto.request.ChatRoomPrivateRequestDto;
import com.sparta.perdayonespoon.websocket.dto.response.ChatMessageResponseDto;
import com.sparta.perdayonespoon.websocket.dto.response.ChatRoomResponseDto;
import com.sparta.perdayonespoon.websocket.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ParticipantRepository participantRepository;
    private final RedisRepository redisRepository;
    private final ChatMessageService chatMessageService;
    private final ChatMessageRepository chatMessageRepository;

    private final SimpMessageSendingOperations messageSendingOperations;

    @Transactional
    public ChatRoomResponseDto createPrivateRoom(Long friendId, Principaldetail principaldetail) {

        if (principaldetail.getMember().getId().equals(friendId))
            throw new IllegalArgumentException("자신한테 채팅할 수 없습니다.");

        // 나와 친구 둘다 채팅방에 참가자로 추가
        Member member = memberRepository.findById(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException("자신을 찾을수가 없습니다.")
        );

        Member friend = memberRepository.findById(friendId).orElseThrow(
                () -> new IllegalArgumentException("친구를 찾을수가 없습니다.")
        );
        // 이미 채팅방 있는지 확인 -> QueryDsl 로 바꾸는게 맞는듯 성능손해 오져보임
        List<ChatRoom> roomList = new ArrayList<>();
        List<Participant> participantList = participantRepository.findAllByMember(member);
        for (Participant p : participantList) {
            roomList.add(p.getChatRoom());
        }
        for (ChatRoom r : roomList) {
            if (r.getType() == Type.PRIVATE) {
                for (Participant p2 : r.getParticipantList()) {
                    if (p2.getMember() == friend) {
                        throw new IllegalArgumentException("이미 채팅방이 만들어져 있습니다.");
                    }
                }
            }
        }

        // 채팅방 생성
        ChatRoom room = chatRoomRepository.save(ChatRoom.privateBuilder()
                .member(friend)
                .build());
        Participant participantMe = Participant.builder()
                .room(room)
                .member(member)
                .build();
        Participant participantYou = Participant.builder()
                .room(room)
                .member(friend)
                .build();
        participantRepository.save(participantMe);
        participantRepository.save(participantYou);
        redisRepository.subscribe(room.getRoomId());
        messageSendingOperations.convertAndSend("/sub/chat/room/1", "가냐고?");
        chatMessageService.sendEnterMessage(room, member);
        chatMessageService.sendEnterMessage(room, friend);
        return ChatRoomResponseDto.builder().room(room).member(member).build();
    }

    @Transactional
    public List<ChatRoomResponseDto> findAllRoom(Principaldetail principaldetail) {
        Member member = memberRepository.findById(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을수가 없습니다.")
        );
        List<ChatRoomResponseDto> responseDtoList = new ArrayList<>();
        List<Participant> participantList = participantRepository.findAllByMember(member);
        for (Participant p : participantList) {
            ChatRoom room = chatRoomRepository.findById(p.getChatRoom().getRoomId()).orElseThrow(
                    () -> new IllegalArgumentException("채팅방이 존재하지 않습니다.")
            );
            ChatRoomResponseDto responseDto = ChatRoomResponseDto.builder().room(room).member(member).build();
            responseDtoList.add(responseDto);
        }
//        responseDtoList = responseDtoList.stream().sorted(Comparator.comparing(ChatRoomResponseDto::getLastMessage)).collect(Collectors.toList());
        Collections.sort(responseDtoList);
        return responseDtoList;
    }

    @Transactional
    public ResponseEntity<List<ChatMessageResponseDto>> findById(String id, Principaldetail principaldetail) {
        Member member = memberRepository.findById(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을수가 없습니다.")
        );
        ChatRoom room = chatRoomRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("채팅방이 존재하지 않습니다.")
        );
        List<ChatMessage> chatMessages = chatMessageRepository.findAllMessageById(room.getRoomId());
        log.info(chatMessages.toString());
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();
        chatMessages.forEach(ChatMessage -> isMeCheck(ChatMessage, chatMessageResponseDtoList, principaldetail.getMember()));
        return ResponseEntity.ok().body(chatMessageResponseDtoList);
    }

    private void isMeCheck(ChatMessage chatMessage, List<ChatMessageResponseDto> chatMessageResponseDtoList, Member member) {
        ChatMessageResponseDto chatMessageResponseDto = ChatMessageResponseDto.standardBuilder().message(chatMessage).build();
        if (chatMessage.getMember() != null) {
            chatMessageResponseDto.isMeCheck(chatMessage.getMember().getNickname().equals(member.getNickname()));
        }
        chatMessageResponseDtoList.add(chatMessageResponseDto);
    }

    @Transactional
    public void exitRoom(ChatRoomExitRequestDto requestDto, Principaldetail principaldetail) {
        Member member = memberRepository.findById(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을수가 없습니다.")
        );
        ChatRoom room = chatRoomRepository.findById(requestDto.getRoomId()).orElseThrow(
                () -> new IllegalArgumentException("채팅방이 존재하지 않습니다.")
        );
        List<Participant> participantList = room.getParticipantList();
        for (Participant p : participantList) {
            if (p.getMember() == member) {
                participantRepository.delete(p);
                break;
            }
        }
        if (room.getParticipantList().isEmpty()) {
            chatRoomRepository.delete(room);
        }
    }

    public ResponseEntity getUserInfo(String roomId, Principaldetail principaldetail) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        return ResponseEntity.ok().body("ㅇㅋ");
    }

//    @Transactional
//    public void enterPublicRoom(ChatRoomEnterRequestDto requestDto, String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(
//                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
//        );
//        ChatRoom room = chatRoomRepository.findById(requestDto.getRoomId()).orElseThrow(
//                () -> new CustomException(ErrorCode.ROOM_NOT_FOUND)
//        );
//        participantRepository.save(new Participant(user, room));
//        chatMessageService.sendEnterMessage(room, user);
//    }
}