package com.sparta.perdayonespoon.sse.domain.entity;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.sse.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiver;

    @Builder
    public Notification(Member receiver, boolean isRead, NotificationType notificationType,String message) {
        this.receiver = receiver;
        this.isRead = isRead;
        this.message = message;
        this.notificationType = notificationType;
    }


    public void read() {
        this.isRead = true;
    }
}
