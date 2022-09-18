package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.ImageDto;
import com.sparta.perdayonespoon.domain.dto.request.PrivateDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.service.PrivateService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@Api(tags="권한 수정 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class PrivateController {
    private final PrivateService privateService;


    @ApiOperation(value = "습관 공개권한 수정 API", notes = "토큰검사 후 습관의 권한 변경하여 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동",response = ImageDto.class),
    })
    @PatchMapping(value = "change/goal/{goalFlag}")
    public ResponseEntity changeImage (@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @RequestBody(required = false) PrivateDto privateDto, @PathVariable String goalFlag){
        return privateService.changePrivateCheck(principaldetail,privateDto,goalFlag);
    }
}
