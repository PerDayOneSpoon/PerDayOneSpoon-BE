package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final MyPageService myPageService;

    @Scheduled(cron = "0 0 1 * * *")
    public void removeImage() {
        myPageService.removeS3Image();
    }

}

