package com.sparta.perdayonespoon.controller;


import com.sparta.perdayonespoon.service.GoogleService;
import com.sparta.perdayonespoon.service.KakaoService;
import com.sparta.perdayonespoon.service.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class AuthController {

    private final KakaoService kakaoService;

    private final GoogleService googleService;
//
    private final NaverService naverService;

//    @Autowired
//    public AuthController(OAuthService oAuthService){
//        this.oAuthService = oAuthService;
//    }
//
//    @GetMapping("/auth/{socialLoginType}") //GOOGLE이 들어올 것이다.
//    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String SocialLoginPath) throws IOException {
//        ProviderType socialLoginType= ProviderType.valueOf(SocialLoginPath.toUpperCase());
//        oAuthService.request(socialLoginType);
//    }

    @GetMapping("/user/login/kakao") // (3)
    public ResponseEntity getkakaoLogin(@RequestParam("code") String code) { //(4)
        return kakaoService.login(code);
    }

    @GetMapping("/user/login/google") // (3)
    public ResponseEntity getgoogleLogin(@RequestParam("code") String code) { //(4)
        return googleService.login(code);
    }

    @GetMapping("/user/login/naver") // (3)
    public ResponseEntity getnaverLogin(@RequestParam("code") String code,
                                        @Nullable @RequestParam(value = "state") String state) { //(4)
        return naverService.login(code,state);
    }
}
