package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags="캘린더 페이지 REST API")
@RequestMapping("api/calender")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class CalenderController {


    @ApiOperation(value = "캘린더 API", notes = "구글로 로그인 하는 apI ")
    @ApiImplicitParam(name = "code", value = "서버로 넘겨주는 인가코드")  // Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                            @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
            @ApiResponse(code = 400, message = "Request타입 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("/test")
    public Principaldetail getPrincipal(@AuthenticationPrincipal Principaldetail principaldetail){
        return principaldetail;
    }
}
