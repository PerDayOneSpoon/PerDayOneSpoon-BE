package com.sparta.perdayonespoon.sse.domain.repository;

import com.sparta.perdayonespoon.sse.dto.NotificationDto;

import java.util.List;

public interface NotificationRepositoryCustom {

    List<NotificationDto> getMessageById(Long userId);

    long changeAllMessage(Long id);

    long changeOneMessage(Long id, Long notificationId);
}
