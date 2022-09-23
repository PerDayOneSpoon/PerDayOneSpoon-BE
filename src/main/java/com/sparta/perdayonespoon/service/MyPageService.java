package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.*;
import com.sparta.perdayonespoon.domain.dto.ImageDto;
import com.sparta.perdayonespoon.domain.dto.S3Dto;
import com.sparta.perdayonespoon.domain.dto.request.StatusDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.MyPageCollectDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.mapper.MemberMapper;
import com.sparta.perdayonespoon.repository.*;
import com.sparta.perdayonespoon.util.GenerateMsg;
import com.sparta.perdayonespoon.util.Scalr_Resize_S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final GoalRepository goalRepository;
    private final FriendRepository friendRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final DeletedUrlPathRepository deletedUrlPathRepository;

    private final MemberRepository memberRepository;

    private final Scalr_Resize_S3Uploader scalr_resize_s3Uploader;

    public ResponseEntity getProfile(Principaldetail principaldetail) {
        //TODO 이룬 목표 개수 , 팔로워한 친구 수 , 팔로우한 친구 수 3개가 가야함 추후엔 뱃지까지 follower -> 나를 팔로우한 사람 following 내가 팔로우한거 이렇게
        MyPageCollectDto myPageCollectDto = memberRepository.getMypageData(principaldetail.getMember().getSocialId());
        myPageCollectDto.SetCodeMsg(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"프로필 조회에 성공하셨습니다."));
        return ResponseEntity.ok(myPageCollectDto);
    }

    public ResponseEntity deleteToken(Principaldetail principaldetail){
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));
        //(6)
        return ResponseEntity.ok(GenerateMsg.getMsg(HttpServletResponse.SC_OK,principaldetail.getMember().getNickname()+"님 로그아웃에 성공하셨습니다."));
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
        return ResponseEntity.ok(GenerateMsg.getMsg(HttpServletResponse.SC_OK,principaldetail.getMember().getNickname()+"님 회원탈퇴 성공하셨습니다."));
    }

    private boolean delete(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
        return true;
    }
    private boolean deleteDb(Member member){
        memberRepository.delete(member);
        return true;
    }

    public ResponseEntity changeImage(Principaldetail principaldetail, MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            throw new IllegalArgumentException("게시글 작성시 이미지 파일이 필요합니다.");
        }
        Member member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId()).orElseThrow(IllegalArgumentException::new);
        S3Dto s3Dto = scalr_resize_s3Uploader.uploadImage(multipartFile);
        DeletedUrlPath deletedUrlPath = DeletedUrlPath.builder().deletedUrlPath(member.getImage().getImgUrl()).build();
        deletedUrlPathRepository.save(deletedUrlPath);
        member.getImage().SetTwoField(s3Dto);
        memberRepository.save(member);
        ImageDto imageDto = ImageDto.builder()
                .imageName(member.getImage().getImgName())
                .uploadImageUrl(member.getImage().getImgUrl())
                .build();
        imageDto.SetTwoproperties(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"이미지 변경에 성공하셨습니다."));
        return ResponseEntity.ok(imageDto);
    }

    public ResponseEntity changeStatus(Principaldetail principaldetail, StatusDto statusDto) {
        Member member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId()).orElseThrow(IllegalArgumentException::new);
        if(statusDto.getStatus() != null && statusDto.getNickname() !=null){
            member.SetTwoColumn(statusDto);
        }else if (statusDto.getNickname() != null){
            member.Setname(statusDto.getNickname());
        }else if (statusDto.getStatus() != null) {
            member.SetStatus(statusDto.getStatus());
        }else if(statusDto.getStatus() == null && statusDto.getNickname() == null){
            throw new IllegalArgumentException("입력을 받지 못했습니다.");
        }

        memberRepository.save(member);
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);
        memberResponseDto.setTwoField(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"성공하셨습니다."));
        return ResponseEntity.ok(memberResponseDto);
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
        if(multipartFile != null) {
            S3Dto s3Dto = scalr_resize_s3Uploader.uploadImage(multipartFile);
            DeletedUrlPath deletedUrlPath = DeletedUrlPath.builder().deletedUrlPath(member.getImage().getImgUrl()).build();
            deletedUrlPathRepository.save(deletedUrlPath);
            member.getImage().SetTwoField(s3Dto);
        }
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);
        memberResponseDto.setTwoField(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"프로필 변경에 성공하셨습니다."));
        return ResponseEntity.ok(memberResponseDto);
    }
}
