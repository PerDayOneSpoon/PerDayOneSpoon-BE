package com.sparta.perdayonespoon.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponseDto {

    private Long goalId;
    private Long commentId;

    private String content;

    private String profileImage;

    private String nickname;

    private String createdAt;

    private boolean isMe;
    @Builder
    public CommentResponseDto(Long goalId,Long commentId, String content, String profileImage,String nickname, String createdAt,boolean isMe){
        this.goalId =goalId;
        this.commentId = commentId;
        this.content = content;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.createdAt =createdAt;
        this.isMe = isMe;
    }
}
