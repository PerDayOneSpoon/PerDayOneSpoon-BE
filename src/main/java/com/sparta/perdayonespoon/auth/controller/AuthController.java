package com.sparta.perdayonespoon.auth.controller;

import com.sparta.perdayonespoon.domain.dto.request.TokenSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.auth.service.GoogleService;
import com.sparta.perdayonespoon.auth.service.KakaoService;
import com.sparta.perdayonespoon.auth.service.NaverService;
import com.sparta.perdayonespoon.util.ReissueUtil;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import java.io.IOException;

@Api(tags="소셜로그인 REST API")
@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final ReissueUtil reissueUtil;

    private final KakaoService kakaoService;

    private final GoogleService googleService;

    private final NaverService naverService;

    @ApiOperation(value = "카카오 로그인 API", notes = "카카오 로그인 하는 apI ")
    @ApiImplicitParam(name = "code", required = true,  dataType = "string", paramType = "query", value = "서버로 넘겨주는 인가코드")  // Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동", response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/kakao") // (3)
    public ResponseEntity<MemberResponseDto> getkakaoLogin(@RequestParam("code") String code) throws MessagingException, IOException {//(4)
        return kakaoService.login(code);
    }

    @ApiOperation(value = "구글 로그인 API", notes = "구글로 로그인 하는 apI ")
    @ApiImplicitParam(name = "code", required = true,  dataType = "string", paramType = "query", value = "서버로 넘겨주는 인가코드")  // Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/google") // (3)
    public ResponseEntity<MemberResponseDto> getgoogleLogin(@RequestParam("code") String code) throws MessagingException, IOException { //(4)
        return googleService.login(code);
    }
    @ApiOperation(value = "네이버 로그인 API", notes = "네이버 로그인 하는 apI ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", required = true,  dataType = "string", paramType = "query", value = "서버로 넘겨주는 인가코드"),
            @ApiImplicitParam(name = "state", required = true,  dataType = "string", paramType = "query", value = "서버로 넘겨주는 인가코드의 상태"),
    })// Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/naver") // (3)
    public ResponseEntity<MemberResponseDto> getnaverLogin(@RequestParam("code") String code,
                                        @Nullable @RequestParam(value = "state") String state) { //(4)
        return naverService.login(code,state);
    }

    @ApiOperation(value = "토큰 재발급 API", notes = "토큰검사 후 access토큰 재발급 또는 예외처리 리턴")
    @ApiImplicitParam(name = "refreshtoken", required = false,  dataType = "string", paramType = "header", value = "refreshtoken header")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class,responseHeaders = @ResponseHeader(name = "accesstoken", description = "accesstoken이 재발행되어 담기는 헤더의 이름", response = TokenDto.class))
    })
    @PostMapping("/reissue")  //재발급을 위한 로직
    public ResponseEntity<MsgDto> reissue(@ApiIgnore @RequestHeader(value = "refreshtoken", required = false) String refreshtoken){
        TokenSearchCondition tokenSearchCondition = new TokenSearchCondition();
        tokenSearchCondition.setRefreshtoken(refreshtoken);
        return reissueUtil.regenerateToken(tokenSearchCondition);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().body("무중단");
    }

}
