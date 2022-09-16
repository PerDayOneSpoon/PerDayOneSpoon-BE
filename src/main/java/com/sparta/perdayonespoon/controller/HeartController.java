package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.HeartService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags="좋아요 및 응원하기 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class HeartController {

    private final HeartService heartService;

    @ApiOperation(value = "습관 좋아요 API", notes = "토큰검사 후 습관에 좋아요 추가")
    @ApiImplicitParam(name = "goalId", required = false,  dataType = "string", paramType = "path", value = "좋아요 할때 전달하는 습관의 Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @PatchMapping ("/heart/{goalId}")
    public ResponseEntity addHeart(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @PathVariable Long goalId){
        return heartService.addHeart(principaldetail,goalId);
    }
}