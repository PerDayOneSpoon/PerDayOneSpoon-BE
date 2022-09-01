package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.service.GoogleService;
import com.sparta.perdayonespoon.service.KakaoService;
import com.sparta.perdayonespoon.service.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

@RequestMapping("user/login")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class AuthController {

    private final KakaoService kakaoService;

    private final GoogleService googleService;
//
    private final NaverService naverService;

    @GetMapping("/kakao") // (3)
    public ResponseEntity getkakaoLogin(@RequestParam("code") String code) {//(4)
        return kakaoService.login(code);
    }

    @GetMapping("/google") // (3)
    public ResponseEntity getgoogleLogin(@RequestParam("code") String code) { //(4)
        return googleService.login(code);
    }

    @GetMapping("/naver") // (3)
    public ResponseEntity getnaverLogin(@RequestParam("code") String code,
                                        @Nullable @RequestParam(value = "state") String state) { //(4)
        return naverService.login(code,state);
    }
}
