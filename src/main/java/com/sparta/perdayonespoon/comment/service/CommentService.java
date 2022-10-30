package com.sparta.perdayonespoon.comment.service;

import com.sparta.perdayonespoon.comment.domain.entity.Comment;
import com.sparta.perdayonespoon.comment.domain.repository.CommentRepository;
import com.sparta.perdayonespoon.comment.dto.CommentRequestDto;
import com.sparta.perdayonespoon.comment.dto.CommentResponseDto;
import com.sparta.perdayonespoon.domain.BadgeSseDto;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.FriendResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.sse.NotificationType;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import com.sparta.perdayonespoon.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ResponseEntity<MsgDto> addComment(Principaldetail principaldetail, Long goalId, CommentRequestDto commentRequestDto) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(
                ()-> new IllegalArgumentException("해당 습관이 없습니다."));
        Member member = memberRepository.getMemberAndImage(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException(" 해당 유저가 없습니다."));
        Comment comment = Comment.builder()
                .goal(goal)
                .member(member)
                .content(commentRequestDto.getContent())
                .nickname(member.getNickname())
                .socialId(member.getSocialId())
                .profileImage(member.getImage().getImgUrl())
                .build();
        if(!goal.getSocialId().equals(member.getSocialId())) {
            String message;
            if (goal.getTitle().length() <= 8) {
                message = member.getNickname() + "님이 " + goal.getTitle() + "에 댓글을 달았습니다.";
            } else {
                message = member.getNickname() + "님이 " + goal.getTitle().substring(0, 8) + "...에 댓글을 달았습니다.";
            }
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Comment)
                    .message(message)
                    .member(goal.getMember())
                    .build());
        }
        commentRepository.save(comment);
        return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("댓글 작성에 성공하셨습니다.").build());
    }

    @Transactional
    public ResponseEntity<MsgDto> deleteComment(Principaldetail principaldetail,Long commentId) {
        Comment comment = commentRepository.getCommentById(commentId).orElseThrow(
                ()-> new IllegalArgumentException("해당 댓글이 없습니다."));
        if(comment.getGoal().getMember().getId().equals(principaldetail.getMember().getId()) && comment.getNickname().equals(principaldetail.getMember().getNickname())){
            commentRepository.deleteById(comment.getId());
            return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("댓글 삭제에 성공하셨습니다.").build());
        }else if(comment.getGoal().getMember().getId().equals(principaldetail.getMember().getId()) && !comment.getNickname().equals(principaldetail.getMember().getNickname())){
            commentRepository.deleteById(comment.getId());
            return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("댓글 삭제에 성공하셨습니다.").build());
        }else if(!comment.getGoal().getMember().getId().equals(principaldetail.getMember().getId()) && comment.getNickname().equals(principaldetail.getMember().getNickname())){
            commentRepository.deleteById(comment.getId());
            return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("댓글 삭제에 성공하셨습니다.").build());
        }
        else throw new IllegalArgumentException("해당 댓글을 지울 수 없습니다.");
    }

    public ResponseEntity<MsgDto> changeComment(Principaldetail principaldetail, Long commentId, CommentRequestDto commentRequestDto) {
        commentRepository.changeComment(principaldetail,commentId,commentRequestDto);
        return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("댓글 변경에 성공하셨습니다.").build());
    }

}
