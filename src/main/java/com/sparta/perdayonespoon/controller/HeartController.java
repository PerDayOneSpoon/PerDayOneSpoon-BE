package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.HeartResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.HeartService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags="좋아요 및 응원하기 REST API")
@RequiredArgsConstructor
@RestController
public class HeartController {

    private final HeartService heartService;

    @ApiOperation(value = "습관 좋아요 새로 생길 API", notes = "토큰검사 후 습관들에 좋아요 추가")
    @ApiImplicitParam(name = "goalFlag", required = false,  dataType = "string", paramType = "path", value = "좋아요 할때 전달하는 습관들 통합 goalFlag")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @PatchMapping("/heart/{goalFlag}")
    public ResponseEntity<HeartResponseDto> addHearts(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @PathVariable String goalFlag){
        return heartService.addHearts(principaldetail,goalFlag);
    }

}
