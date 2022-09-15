package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.service.FriendService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags="친구 페이지 REST API")
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
public class FriendController {

    private final FriendService friendService;

    private final MemberRepository memberRepository;

    @ApiOperation(value = "친구 검색 API", notes = "토큰검사 후 친구 검색해서 응답")
    @ApiImplicitParam(name = "searchQuery", required = false,  dataType = "string", paramType = "path", value = "검색할 때 입력하는 변수명")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @GetMapping("/search/friends/{searchQuery}")
    public ResponseEntity<List<MemberSearchDto>> getFriendlist(@PathVariable String searchQuery){
        MemberSearchCondition memberSearchCondition = new MemberSearchCondition(searchQuery);
        return ResponseEntity.ok().body(memberRepository.getMember(memberSearchCondition));
    }

    @ApiOperation(value = "친구 추가 API", notes = "토큰검사 후 친구를 추가")
    @ApiImplicitParam(name = "friendsId", required = false,  dataType = "string", paramType = "path", value = "추가할때 요청하는 친구의 Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @PostMapping("/friends/{friendsId}")
    public ResponseEntity addFriend(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @PathVariable String friendsId){
        return friendService.addFriend(principaldetail,friendsId);
    }

    @ApiOperation(value = "친구 삭제 API", notes = "토큰검사 후 친구를 삭제")
    @ApiImplicitParam(name = "friendsId", required = false,  dataType = "string", paramType = "path", value = "추가할때 요청하는 친구의 Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @DeleteMapping("delete/friend/{friendId}")
    public ResponseEntity deleteFriend(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,@PathVariable String friendId){
        return friendService.deleteFriend(principaldetail,friendId);
    }
}
