package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.DeletedUrlPath;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.RefreshToken;
import com.sparta.perdayonespoon.domain.dto.ImageDto;
import com.sparta.perdayonespoon.domain.dto.S3Dto;
import com.sparta.perdayonespoon.domain.dto.request.StatusDto;
import com.sparta.perdayonespoon.domain.dto.response.MemberResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.mapper.MemberMapper;
import com.sparta.perdayonespoon.repository.DeletedUrlPathRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.repository.RefreshTokenRepository;
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
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class MyPageService {


    private final RefreshTokenRepository refreshTokenRepository;

    private final DeletedUrlPathRepository deletedUrlPathRepository;

    private final MemberRepository memberRepository;

    private final Scalr_Resize_S3Uploader scalr_resize_s3Uploader;

    public ResponseEntity getProfile(Principaldetail principaldetail) {
        Optional<Member> member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId());
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member.orElseThrow(()-> new IllegalArgumentException("유저정보가 일치하지 않습니다.")));
        memberResponseDto.setTwoField(GenerateMsg.getMsg(HttpServletResponse.SC_OK,memberResponseDto.getNickname()+"프로필 조회에 성공하셨습니다."));
        return ResponseEntity.ok(memberResponseDto);
    }

    public ResponseEntity deleteToken(Principaldetail principaldetail){
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));
        return ResponseEntity.ok(GenerateMsg.getMsg(HttpServletResponse.SC_OK,principaldetail.getMember().getNickname()+"님 로그아웃에 성공하셨습니다."));
    }

    public ResponseEntity deleteMember(Principaldetail principaldetail) {
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));
        memberRepository.findBySocialId(principaldetail.getMember().getSocialId())
                .map(this::deleteDb)
                .orElseThrow(() -> new IllegalArgumentException("이미 탈퇴한 회원입니다."));
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

    @Transactional
    public ResponseEntity changeImage(Principaldetail principaldetail, MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            throw new IllegalArgumentException("게시글 작성시 이미지 파일이 필요합니다.");
        }
        Optional<Member> member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId());
        S3Dto s3Dto = scalr_resize_s3Uploader.uploadImage(multipartFile);
        DeletedUrlPath deletedUrlPath = DeletedUrlPath.builder().deletedUrlPath(member.get().getImage().getImgUrl()).build();
        deletedUrlPathRepository.save(deletedUrlPath);
        member.orElseThrow(()->new IllegalArgumentException("유저가 없습니다.")).getImage().SetTwoField(s3Dto);
        ImageDto imageDto = ImageDto.builder()
                .imageName(member.get().getImage().getImgName())
                .uploadImageUrl(member.get().getImage().getImgUrl())
                .build();
        imageDto.SetTwoproperties(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"이미지 변경에 성공하셨습니다."));
        return ResponseEntity.ok(imageDto);
    }

    @Transactional
    public ResponseEntity changeStatus(Principaldetail principaldetail, StatusDto statusDto) {
        Optional<Member> member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId());
        if(statusDto.getStatus() != null && statusDto.getNickname() !=null){
            member.get().SetTwoColumn(statusDto.getNickname(),statusDto.getStatus());
        }else if (statusDto.getNickname() != null){
            member.orElseThrow(()-> new IllegalArgumentException("사용자가 없습니다.")).Setname(statusDto.getNickname());
        } else if (statusDto.getStatus() != null) {
            member.orElseThrow(()-> new IllegalArgumentException("사용자가 없습니다.")).SetStatus(statusDto.getStatus());
        }
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member.get());
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
}
