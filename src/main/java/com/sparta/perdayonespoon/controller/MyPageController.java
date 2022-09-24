package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.StatusDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.MyPageService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@Api(tags = "마이페이지 REST API")
@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    @ApiOperation(value = "마이페이지 유저정보 확인 API", notes = "토큰검사 후 유저정보 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
    })
    @GetMapping("confirm/profile")
    public ResponseEntity getProfile(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return myPageService.getProfile(principaldetail);
    }

    @ApiOperation(value = "마이페이지 사진 , 프로필 변경 API", notes = "토큰검사 후 사진 및 프로필 변경한 뒤 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MemberResponseDto.class),
    })
    @PatchMapping(value = "change/profile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity changeProfile (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,
                                         @RequestPart(value = "dto" , required = false) StatusDto statusDto,
                                         @RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        return myPageService.changeProfile(principaldetail,multipartFile,statusDto);
    }

    @ApiOperation(value = "마이페이지 로그아웃 API", notes = "토큰검사 후 유저정보 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
    })
    @DeleteMapping("delete/user/logout")
    public ResponseEntity deleteToken (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return myPageService.deleteToken(principaldetail);
    }

    @ApiOperation(value = "마이페이지 회원탈퇴 API", notes = "토큰검사 후 유저정보 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
    })
    @DeleteMapping("delete/user/register")
    public ResponseEntity deleteMember (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return myPageService.deleteMember(principaldetail);
    }

}
