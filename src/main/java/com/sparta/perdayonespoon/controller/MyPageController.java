package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.StatusDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
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
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    @ApiOperation(value = "마이페이지 유저정보 확인 API", notes = "토큰검사 후 유저정보 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
            @ApiResponse(code = 400, message = "Request타입 에러, 토큰이 없을때 에러"),
            @ApiResponse(code = 401, message = "변조된 토큰 에러"),
            @ApiResponse(code = 408, message = "만료된 토큰 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @GetMapping("confirm/profile")
    public ResponseEntity getProfile(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return myPageService.getProfile(principaldetail);
    }

    @ApiOperation(value = "마이페이지 사진변경 API", notes = "토큰검사 후 사진 변경한 뒤 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
            @ApiResponse(code = 400, message = "Request타입 에러, 토큰이 없을때 에러"),
            @ApiResponse(code = 401, message = "변조된 토큰 에러"),
            @ApiResponse(code = 408, message = "만료된 토큰 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping(value = "change/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity changeImage (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @RequestPart(required = false) MultipartFile multipartFile)throws IOException {
        return myPageService.changeImage(principaldetail,multipartFile);
    }

    @ApiOperation(value = "마이페이지 상태메세지 변경 API", notes = "토큰검사 후 사진 변경한 뒤 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
            @ApiResponse(code = 400, message = "Request타입 에러, 토큰이 없을때 에러"),
            @ApiResponse(code = 401, message = "변조된 토큰 에러"),
            @ApiResponse(code = 408, message = "만료된 토큰 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @PatchMapping("change/status")
    public ResponseEntity changeStatus (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, StatusDto statusDto){
        return myPageService.changeStatus(principaldetail,statusDto);
    }

    @ApiOperation(value = "마이페이지 로그아웃 API", notes = "토큰검사 후 유저정보 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
            @ApiResponse(code = 400, message = "Request타입 에러, 토큰이 없을때 에러"),
            @ApiResponse(code = 401, message = "변조된 토큰 에러"),
            @ApiResponse(code = 408, message = "만료된 토큰 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @DeleteMapping("delete/user/logout")
    public ResponseEntity deleteToken (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return myPageService.deleteToken(principaldetail);
    }

    @ApiOperation(value = "마이페이지 회원탈퇴 API", notes = "토큰검사 후 유저정보 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = MsgDto.class),
            @ApiResponse(code = 400, message = "Request타입 에러, 토큰이 없을때 에러"),
            @ApiResponse(code = 401, message = "변조된 토큰 에러"),
            @ApiResponse(code = 408, message = "만료된 토큰 에러"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @DeleteMapping("delete/user/register")
    public ResponseEntity deleteMember (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return myPageService.deleteMember(principaldetail);
    }

}
