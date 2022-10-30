package com.sparta.perdayonespoon.sse.listener;

import com.sparta.perdayonespoon.domain.BadgeSseDto;
import com.sparta.perdayonespoon.sse.domain.entity.Notification;
import com.sparta.perdayonespoon.sse.domain.repository.NotificationRepository;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void handleNotification(BadgeSseDto badgeSseDto){
        notificationService.send(badgeSseDto);
    }

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT )
//    @Async
//    public void testRegister(BadgeSseDto badgeSseDto){
//        Notification notification = Notification.builder()
//                .receiver(badgeSseDto.getMember())
//                .notificationType(badgeSseDto.getNotificationType())
//                .message(badgeSseDto.getMessage())
//                .isRead(false) // 현재 읽음상태
//                .build();
//        notificationRepository.save(notification);
//    }

}
