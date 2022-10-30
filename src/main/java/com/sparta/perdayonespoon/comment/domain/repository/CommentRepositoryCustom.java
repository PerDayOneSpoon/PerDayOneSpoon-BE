package com.sparta.perdayonespoon.comment.domain.repository;

import com.sparta.perdayonespoon.comment.domain.entity.Comment;
import com.sparta.perdayonespoon.comment.dto.CommentRequestDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {


    Optional<Comment> getCommentById(Long commentId);

    List<Comment> getCommentByMemberId(Long id);

    long changeComment(Principaldetail principaldetail, Long commentId, CommentRequestDto commentRequestDto);
}
