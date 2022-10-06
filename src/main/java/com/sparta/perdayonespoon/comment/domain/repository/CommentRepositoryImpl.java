package com.sparta.perdayonespoon.comment.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.comment.domain.entity.Comment;

import com.sparta.perdayonespoon.domain.QGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.perdayonespoon.comment.domain.entity.QComment.comment;
import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static com.sparta.perdayonespoon.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Comment> getCommentById(Long commentId){
        return Optional.ofNullable(queryFactory
                .selectFrom(comment)
                .where(comment.id.eq(commentId))
                .innerJoin(comment.member, member).fetchJoin()
                .innerJoin(comment.goal, goal).fetchJoin()
                .fetchOne());
    }
    @Override
    public List<Comment> getCommentByMemberId(Long id){
        return  queryFactory
                .selectFrom(comment)
                .where(comment.member.id.eq(id))
                .fetch();
    }

}
