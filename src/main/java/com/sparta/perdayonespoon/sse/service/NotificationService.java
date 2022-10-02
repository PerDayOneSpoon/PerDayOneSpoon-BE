package com.sparta.perdayonespoon.sse.service;

import com.sparta.perdayonespoon.domain.BadgeSseDto;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.sse.NotificationType;
import com.sparta.perdayonespoon.sse.domain.entity.Notification;
import com.sparta.perdayonespoon.sse.domain.repository.EmitterRepository;
import com.sparta.perdayonespoon.sse.domain.repository.NotificationRepository;
import com.sparta.perdayonespoon.sse.dto.NotificationDto;
import com.sparta.perdayonespoon.sse.dto.NotificationDumyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

//    private final ChatMessageRepository messageRepository;

    public SseEmitter subscribe(Member member, String lastEventId)  {
        Long userId = member.getId();
        //emitter 하나하나 에 고유의 값을 주기 위해
        String emitterId = makeTimeIncludeId(userId);


        Long timeout = 60L * 1000L * 60L; // 1시간
        // 생성된 emiiterId를 기반으로 emitter를 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));

        //emitter의 시간이 만료된 후 레포에서 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        if(!emitterRepository.findAllEmitterStartWithByMemberId(member.getSocialId()).isEmpty()){
            log.info("여기 오긴 왔냐?");
            SseEmitter sseEmitter = emitterRepository.findAllEmitterStartWithByMemberId(member.getSocialId()).get(member.getSocialId()+1);
            String message = member.getNickname()+ "님 회원가입을 환영합니다. 발송된 이메일도 확인해보세요!! 📧";
            BadgeSseDto badgeSseDto =BadgeSseDto.builder()
                    .notificationType(NotificationType.Notice)
                    .message(message)
                    .member(member)
                    .build();
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .reconnectTime(500)
                    .data(badgeSseDto,MediaType.APPLICATION_JSON);
            try{
                emitter.send(eventBuilder);
            }catch (IOException exception) {
                emitterRepository.deleteById(emitterId);
                log.error("sse 연결오류!!!", exception);
            }
            emitterRepository.deleteAllEmitterStartWithId(member.getSocialId());
        }

        // 503 에러를 방지하기 위해 처음 연결 진행 시 더미 데이터를 전달
        String eventId = makeTimeIncludeId(userId);
        NotificationDumyDto notificationDumyDto = NotificationDumyDto.builder()
                .lastEventId(eventId)
                .build();

        // 수 많은 이벤트 들을 구분하기 위해 이벤트 ID에 시간을 통해 구분을 해줌
        sendNotification(emitter, eventId, emitterId, notificationDumyDto);

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }else if(hasOutData(userId)){
            sendOutData(userId,emitterId,emitter);
        }
        return emitter;
    }


    // SseEmitter를 구분 -> 구분자로 시간을 사용함 ,
    // 시간을 붙혀주는 이유 -> 브라우저에서 여러개의 구독을 진행 시
    //탭 마다 SssEmitter 구분을 위해 시간을 붙여 구분하기 위해 아래와 같이 진행
    private String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    // 유효시간이 다 지난다면 503 에러가 발생하기 때문에 더미데이터를 발행
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                .id(eventId)
                .reconnectTime(500)
                .data(data,MediaType.APPLICATION_JSON);
        try {
            emitter.send(eventBuilder);
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            log.error("sse 연결오류!!!", exception);
        }
    }

    // Last - event - id 가 존재한다는 것은 받지 못한 데이터가 있다는 것이다.
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private boolean hasOutData(Long userId) {
        return !emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userId)).isEmpty();
    }
    private void sendOutData(Long userId, String emitterId, SseEmitter emitter){
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userId));
        eventCaches.forEach((key, value) -> sendNotification(emitter,key,emitterId,value));
    }

    // 받지못한 데이터가 있다면 last - event - id를 기준으로 그 뒤의 데이터를 추출해 알림을 보내주면 된다.
    private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userId));
        emitterRepository.deleteAllEventCacheStartWithId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    // =============================================
    /*
        : 실제 다른 사용자가 알림을 보낼 수 있는 기능이 필요
        알림을 구성 후 해당 알림에 대한 이벤트를 발생
        -> 어떤 회원에게 알림을 보낼지에 대해 찾고 알림을
        받을 회원의 emitter들을 모두 찾아 해당 emitter를 Send
     */

    @Async
    @TransactionalEventListener
    public void send(BadgeSseDto badgeSseDto) {
        Notification notification = notificationRepository.save(createNotification(badgeSseDto.getMember(),badgeSseDto.getMessage(),badgeSseDto.getNotificationType()));
        NotificationDto notificationDto = NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.isRead())
                .build();
        String receiverId = String.valueOf(badgeSseDto.getMember().getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notificationDto);
                    sendNotification(emitter, eventId, key, notificationDto);
                }
        );
    }

    private Notification createNotification(Member receiver,String message,NotificationType notificationType) {
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .message(message)
                .isRead(false) // 현재 읽음상태
                .build();
    }

    @Async
    public void sender(Member sender) {
        Notification notification = notificationRepository.save(createNotificationer(sender));
        NotificationDto notificationDto = NotificationDto.builder().id(notification.getId()).build();
        String senderId = String.valueOf(sender.getId());
        String eventId = senderId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(senderId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, notificationDto.getId());
                }
        );
    }

    private Notification createNotificationer(Member sender) {
        return Notification.builder()
                .receiver(sender)
                .isRead(false) // 현재 읽음상태
                .build();
    }

    public ResponseEntity getAllSse(Principaldetail principaldetail) {
        return ResponseEntity.ok().body("이거잖아");
    }
}