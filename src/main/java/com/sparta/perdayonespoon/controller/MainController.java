package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.CalenderResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.MainService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.junit.Ignore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Api(tags="메인페이지 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class MainController {

    private final MainService mainService;

    @ApiOperation(value = "메인페이지 주간 달성률, 목표 확인 API ", notes = "토큰검사 후 주간 달성률과 당일 목표들 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = CalenderResponseDto.class),
    })
    @GetMapping("/confirm/goal")
    public ResponseEntity getGoal(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        return mainService.getGoal(principaldetail);
    }

    @ApiOperation(value = "메인페이지 목표 만들기 API ", notes = "토큰검사 후 만든 목표 응답")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = GoalDto.class)
    })
    @PostMapping("/create")
    public ResponseEntity CreateGoal(@ApiParam @RequestBody GoalDto goalDto, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        return mainService.CreateGoal(goalDto,principaldetail);
    }
}
