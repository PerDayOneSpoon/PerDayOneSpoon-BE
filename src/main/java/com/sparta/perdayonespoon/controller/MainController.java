package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.RequestBooleanDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.MainService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags="메인페이지 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class MainController {

    private final MainService mainService;

    @ApiOperation(value = "메인페이지 주간 달성률, 목표 확인 API ", notes = "토큰검사 후 주간 달성률과 당일 목표들 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동"),
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

    @ApiOperation(value = "메인페이지 목표 완료 여부 수정 API ", notes = "토큰검사 후 해당 목표 완료 여부 수정 후 응답")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = GoalResponseDto.class)
    })
    @PatchMapping("/change/{goalId}")
    public ResponseEntity<GoalResponseDto> ChangeGoal(@PathVariable long goalId, @RequestBody RequestBooleanDto requestBooleanDto, @ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail) {
        return mainService.ChangeGoal(goalId,requestBooleanDto.getAchivement(),principaldetail);
    }

    @ApiOperation(value = "메인페이지 습관 지우기 API ", notes = "토큰검사 후 습관 카테고리 지움")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = GoalDto.class)
    })
    @DeleteMapping("/delete/{deleteFlag}")
    public ResponseEntity deleteGoals(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @PathVariable String deleteFlag) {
        return mainService.deleteGoals(principaldetail,deleteFlag);
    }
}
