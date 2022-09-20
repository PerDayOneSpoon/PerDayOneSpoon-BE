package com.sparta.perdayonespoon.controller;

import com.sparta.perdayonespoon.domain.dto.request.MemberSearchCondition;
import com.sparta.perdayonespoon.domain.dto.response.FriendResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberSearchDto;
import com.sparta.perdayonespoon.domain.follow.FollowResponseDto;
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
public class FriendController {

    private final FriendService friendService;

    private final MemberRepository memberRepository;

    @ApiOperation(value = "친구 검색 API", notes = "토큰검사 후 친구 검색해서 응답")
    @ApiImplicitParam(name = "searchQuery", required = false,  dataType = "string", paramType = "path", value = "검색할 때 입력하는 변수명")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = MemberSearchDto.class)
    })
    @GetMapping("/search/friends/{searchQuery}")
    public ResponseEntity<List<MemberSearchDto>> getFriendlist(@AuthenticationPrincipal Principaldetail principaldetail, @ModelAttribute MemberSearchCondition memberSearchCondition){
        return ResponseEntity.ok().body(memberRepository.getMember(memberSearchCondition,principaldetail.getMember().getSocialId()));
    }

    @ApiOperation(value = "마이페이지 팔로잉 한 유저 검색 API", notes = "토큰검사 후 팔로잉 한 유저 리스트 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FollowResponseDto.class)
    })
    @GetMapping("/search/friends/following")
    public ResponseEntity<FollowResponseDto> getFollowerList(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return friendService.getFollowerList(principaldetail);
    }

    @ApiOperation(value = "마이페이지 팔로워 한 유저 검색 API", notes = "토큰검사 후 팔로워 한 유저 리스트 응답")
    @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FollowResponseDto.class)
    })
    @GetMapping("/search/friends/follower")
    public ResponseEntity<FollowResponseDto> getFollowingList(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail){
        return friendService.getFollowingList(principaldetail);
    }

    @ApiOperation(value = "친구 추가 API", notes = "토큰검사 후 친구를 추가")
    @ApiImplicitParam(name = "friendId", required = false,  dataType = "string", paramType = "path", value = "추가할때 요청하는 친구의 Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FriendResponseDto.class)
    })
    @PostMapping("/friends/{friendId}")
    public ResponseEntity<FriendResponseDto> addFriend(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @PathVariable String friendId){
        return friendService.addFriend(principaldetail,friendId);
    }

    @ApiOperation(value = "팔로잉 한 친구 삭제 API", notes = "토큰검사 후 팔로잉 한 친구를 삭제")
    @ApiImplicitParam(name = "friendId", required = false,  dataType = "string", paramType = "path", value = "팔로우 한 친구 삭제할때 요청하는 친구의 Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FriendResponseDto.class)
    })
    @DeleteMapping("delete/following/{friendId}")
    public ResponseEntity<FriendResponseDto> deleteFollowerFriend(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,@PathVariable String friendId){
        return friendService.deleteFollowerFriend(principaldetail,friendId);
    }

    @ApiOperation(value = "팔로워 된 친구 삭제 API", notes = "토큰검사 후 팔로워 된 친구를 삭제")
    @ApiImplicitParam(name = "friendId", required = false,  dataType = "string", paramType = "path", value = "팔로우 한 친구 삭제할때 요청하는 친구의 Id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FriendResponseDto.class)
    })
    @DeleteMapping("delete/follower/{friendId}")
    public ResponseEntity<FriendResponseDto> deleteFollowingFriend(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,@PathVariable String friendId){
        return friendService.deleteFollowingFriend(principaldetail,friendId);
    }

}
