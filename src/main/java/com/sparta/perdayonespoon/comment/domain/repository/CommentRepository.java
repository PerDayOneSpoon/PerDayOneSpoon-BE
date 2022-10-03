package com.sparta.perdayonespoon.comment.domain.repository;

import com.sparta.perdayonespoon.comment.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>,CommentRepositoryCustom {
}
