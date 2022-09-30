package com.sparta.perdayonespoon.sse.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements  NotificationRepositoryCustom{

    private final JPAQueryFactory queryFactory;
}
