package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.CalenderService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;


@Api(tags="캘린더 페이지 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class CalenderController {
    private final CalenderService calenderService;
    @ApiOperation(value = "캘린더 데이터 조회 API", notes = "캘린더 페이지에서 전체 정보를 보여주는 api ")
    @ApiImplicitParam(name = "code", value = "서버로 넘겨주는 인가코드")  // Swagger에 사용하는 파라미터에 대해 설명
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = {@ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class),
                                       @ResponseHeader(name = "refreshtoken", description = "refreshtoken이 담기는 헤더의 이름", response = TokenDto.class)}),
    })
    @GetMapping("/confirm/calender")
    public ResponseEntity getPrincipal(@AuthenticationPrincipal Principaldetail principaldetail){
        return calenderService.getAlldate(principaldetail);
    }
}
