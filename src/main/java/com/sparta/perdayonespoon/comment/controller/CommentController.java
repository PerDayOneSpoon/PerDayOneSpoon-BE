package com.sparta.perdayonespoon.comment.controller;

import com.sparta.perdayonespoon.comment.dto.CommentRequestDto;
import com.sparta.perdayonespoon.domain.dto.response.FriendResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.comment.service.CommentService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags="댓글 REST API")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "캘린더 페이지 습관 댓글 추가 API", notes = "토큰검사 후 습관 댓글 추가")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
            @ApiImplicitParam(name = "goalId", required = false,  dataType = "Long", paramType = "path", value = "추가할때 요청하는 습관의 Id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FriendResponseDto.class)
    })
    @PostMapping("/create/comment/{goalId}")
    public ResponseEntity<MsgDto> addComment(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail, @PathVariable Long goalId, @RequestBody CommentRequestDto commentRequestDto){
        return commentService.addComment(principaldetail,goalId,commentRequestDto);
    }

    @ApiOperation(value = "캘린더 페이지 습관 댓글 삭제 API", notes = "토큰검사 후 습관 댓글 삭제")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", required = false,  dataType = "string", paramType = "header", value = "accesstoken이 담기는 헤더이름"),
            @ApiImplicitParam(name = "commentId", required = false,  dataType = "Long", paramType = "path", value = "습관 댓글 삭제할때 요청하는 댓글의 Id")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동" , response = FriendResponseDto.class)
    })
    @DeleteMapping("/delete/comment/{commentId}")
    public ResponseEntity<MsgDto> deleteComment(@ApiIgnore @AuthenticationPrincipal Principaldetail principaldetail,@PathVariable Long commentId){
        return commentService.deleteComment(principaldetail,commentId);
    }
}
