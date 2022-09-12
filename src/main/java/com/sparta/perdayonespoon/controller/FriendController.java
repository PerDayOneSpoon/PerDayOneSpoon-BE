package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.domain.dto.response.TokenDto;
import com.sparta.perdayonespoon.repository.MemberRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags="친구 페이지 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class FriendController {

    private final MemberRepository memberRepository;

    @ApiOperation(value = "친구 검색 API", notes = "토큰검사 후 친구 검색해서 응답")
    @ApiImplicitParam(name = "searchQuery", required = false,  dataType = "string", paramType = "path", value = "검색할 때 입력하는 변수명")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @GetMapping("/search/friends/{searchQuery}")
    public List<MemberSearchDto> getFriendlist(@PathVariable String searchQuery){
        MemberSearchCondition memberSearchCondition = new MemberSearchCondition(searchQuery);
        return memberRepository.getMember(memberSearchCondition);
    }
}
