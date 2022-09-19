package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final MyPageService myPageService;

    @Scheduled(cron = "0 0 1 * * *")
    public void removeImage() {
        myPageService.removeS3Image();
    }


//    @Scheduled(cron = "*/5 * * * * *")      // 테스트용 스케줄러, 5초 마다 현재시각 출력
//    public void testQuartz() {
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(now.getHour() + ":" + now.getMinute() + ":" + now.getSecond());
//    }
}


