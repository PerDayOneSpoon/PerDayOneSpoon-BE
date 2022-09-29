package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.BadgeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags="뱃지 REST API ")
@RequestMapping("/confirm")
@RequiredArgsConstructor
@RestController
public class BadgeController {
    private final BadgeService badgeService;

    @ApiOperation(value = "나의 뱃지 목록 조회 API", notes = "뱃지 페이지에서 모은 뱃지를 보여주는 api ")
    @ApiImplicitParam(name = "Authorization",  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses(
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class,
                    responseHeaders = @ResponseHeader(name = "Authorization", description = "accesstoken이 담기는 헤더의 이름", response = TokenDto.class)))
    @GetMapping("/badge")
    public ResponseEntity getAllBadge(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return badgeService.checkAllBadge(principaldetail);
    }
}
