package com.sparta.perdayonespoon.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @NotBlank(message = "댓글 작성시 내용을 입력해야 합니다.")
    private String content;

}
