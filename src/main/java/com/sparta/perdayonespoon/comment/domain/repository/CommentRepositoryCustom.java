package com.sparta.perdayonespoon.comment.domain.repository;

import com.sparta.perdayonespoon.comment.domain.entity.Comment;

import java.util.Optional;

public interface CommentRepositoryCustom {


    Optional<Comment> getCommentById(Long commentId);
}
