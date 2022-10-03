package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.comment.domain.entity.Comment;
import com.sparta.perdayonespoon.comment.domain.repository.CommentRepository;
import com.sparta.perdayonespoon.domain.*;
import com.sparta.perdayonespoon.domain.dto.S3Dto;
import com.sparta.perdayonespoon.domain.dto.request.StatusDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.MyPageCollectDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.mapper.MemberMapper;
import com.sparta.perdayonespoon.repository.*;
import com.sparta.perdayonespoon.util.Scalr_Resize_S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final CommentRepository commentRepository;
    private final GoalRepository goalRepository;
    private final FriendRepository friendRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DeletedUrlPathRepository deletedUrlPathRepository;
    private final MemberRepository memberRepository;
    private final Scalr_Resize_S3Uploader scalr_resize_s3Uploader;

    @Transactional(readOnly = true)
    public ResponseEntity getProfile(Principaldetail principaldetail) {
        //TODO 이룬 목표 개수 , 팔로워한 친구 수 , 팔로우한 친구 수 3개가 가야함 추후엔 뱃지까지 follower -> 나를 팔로우한 사람 following 내가 팔로우한거 이렇게
        MyPageCollectDto myPageCollectDto = memberRepository.getMypageData(principaldetail.getMember().getSocialId());
        myPageCollectDto.SetCodeMsg(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("프로필 조회에 성공하셨습니다.").build());
        return ResponseEntity.ok(myPageCollectDto);
    }

    public ResponseEntity deleteToken(Principaldetail principaldetail){
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));
        //(6)
        return ResponseEntity.ok(MsgDto.builder().code(HttpServletResponse.SC_OK).msg(principaldetail.getMember().getNickname()+"님 로그아웃에 성공하셨습니다.").build());
    }
    public ResponseEntity deleteMember(Principaldetail principaldetail) {
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));
        memberRepository.findBySocialId(principaldetail.getMember().getSocialId())
                .map(this::deleteDb)
                .orElseThrow(() -> new IllegalArgumentException("이미 탈퇴한 회원입니다."));
        List<Goal> goalList = goalRepository.findAllBySocialId(principaldetail.getMember().getSocialId());
        List<Friend> friends = friendRepository.findAllByFollowingId(principaldetail.getMember().getSocialId());
        List<Friend> friendList = friendRepository.findAllByFollowerId(principaldetail.getMember().getSocialId());
        friendRepository.deleteAll(friends);
        friendRepository.deleteAll(friendList);
        goalRepository.deleteAll(goalList);
        return ResponseEntity.ok(MsgDto.builder().code(HttpServletResponse.SC_OK).msg(principaldetail.getMember().getNickname()+"님 회원탈퇴 성공하셨습니다.").build());
    }

    private boolean delete(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
        return true;
    }
    private boolean deleteDb(Member member){
        memberRepository.delete(member);
        return true;
    }
    public void removeS3Image() {
        List<DeletedUrlPath> deletedUrlPaths = deletedUrlPathRepository.findAll();
        deletedUrlPaths.forEach(this::remove);
        deletedUrlPathRepository.deleteAll();
    }
    private void remove(DeletedUrlPath deletedUrlPath){
        scalr_resize_s3Uploader.remove(deletedUrlPath.getDeletedUrlPath());
    }

    @Transactional
    public ResponseEntity changeProfile(Principaldetail principaldetail, MultipartFile multipartFile, StatusDto statusDto) throws IOException {
        Member member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId()).orElseThrow(IllegalArgumentException::new);
        if(statusDto.getStatus() != null && statusDto.getNickname() != null){
            member.SetTwoColumn(statusDto);
        }else if (statusDto.getNickname() != null){
            member.Setname(statusDto.getNickname());
        }else if (statusDto.getStatus() != null) {
            member.SetStatus(statusDto.getStatus());
        }
        List<Comment> commentList = commentRepository.getCommentByMemberId(principaldetail.getMember().getId());
        if(multipartFile != null) {
            S3Dto s3Dto = scalr_resize_s3Uploader.uploadImage(multipartFile);
            DeletedUrlPath deletedUrlPath = DeletedUrlPath.builder().deletedUrlPath(member.getImage().getImgUrl()).build();
            deletedUrlPathRepository.save(deletedUrlPath);
            member.getImage().SetTwoField(s3Dto);
            if(!commentList.isEmpty()) {
                commentList.forEach(comment -> changeImageandName(comment, s3Dto.getUploadImageUrl(), statusDto.getNickname()));
                commentRepository.saveAll(commentList);
            }
        } else if(!commentList.isEmpty()) {
            commentList.forEach(comment -> changeName(comment, statusDto.getNickname()));
            commentRepository.saveAll(commentList);
        }
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);
        memberResponseDto.setTwoField(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("프로필 변경에 성공하셨습니다.").build());
        return ResponseEntity.ok(memberResponseDto);
    }

    private void changeImageandName(Comment comment , String profileImage, String nickname){
        comment.changeImageandName(profileImage,nickname);
    }

    private void changeName(Comment comment, String nickname) {
        comment.changeName(nickname);
    }
}
