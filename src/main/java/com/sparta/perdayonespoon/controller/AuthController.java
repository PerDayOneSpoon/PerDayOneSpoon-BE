package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.service.GoogleService;
import com.sparta.perdayonespoon.service.KakaoService;
import com.sparta.perdayonespoon.service.NaverService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
//@Tag(name = "user", description = "사용자 API")
@Api(tags="소셜로그인 REST API")
@RequestMapping("/user/login")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class AuthController {

    private final KakaoService kakaoService;

    private final GoogleService googleService;

    private final NaverService naverService;

    @ApiOperation(value = "카카오 로그인 API", notes = "카카오 로그인 하는 apI ")
    @ApiImplicitParam(name = "code", value = "서버로 넘겨주는 인가코드")  // Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동", response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
            @ApiResponse(code = 400, message = "Request타입 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/kakao") // (3)
    public ResponseEntity getkakaoLogin(@RequestParam("code") String code) {//(4)
        return kakaoService.login(code);
    }

    @ApiOperation(value = "구글 로그인 API", notes = "구글로 로그인 하는 apI ")
    @ApiImplicitParam(name = "code", value = "서버로 넘겨주는 인가코드")  // Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
            @ApiResponse(code = 400, message = "Request타입 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/google") // (3)
    public ResponseEntity getgoogleLogin(@RequestParam("code") String code) { //(4)

        System.out.println("구글0번로그인");
        System.out.println(code);
        return googleService.login(code);
    }
    @ApiOperation(value = "네이버 로그인 API", notes = "네이버 로그인 하는 apI ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "서버로 넘겨주는 인가코드"),
            @ApiImplicitParam(name = "state", value = "서버로 넘겨주는 인가코드의 상태")
    })// Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
            @ApiResponse(code = 400, message = "Request타입 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/naver") // (3)
    public ResponseEntity getnaverLogin(@RequestParam("code") String code,
                                        @Nullable @RequestParam(value = "state") String state) { //(4)
        return naverService.login(code,state);
    }

//    @ApiOperation(value = "토큰 재발급 API", notes = "토큰검사 후 access토큰 재발급 또는 예외처리 리턴")
//    @ApiImplicitParam(name = "Refreshtoken", value = "재발급을 위해 검증할 refreshtoken 요청")  // Swagger에 사용하는 파라미터에 대해 설명
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "API 정상 작동"),
//            @ApiResponse(code = 400, message = "Request타입 에러"),
//            @ApiResponse(code = 500, message = "서버 에러")
//    })
//    @PostMapping("/reissue")  //재발급을 위한 로직
//    public ResponseEntity<TokenDto> reissue( @RequestHeader(value = "refreshtoken", required = false) String identification){
//        return authService.issue(identification);
//        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
//    }
}
