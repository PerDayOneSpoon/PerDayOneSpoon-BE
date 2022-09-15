package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class MyPageService {
    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String KAKAO_SNS_CLIENT_ID;
    private final RefreshTokenRepository refreshTokenRepository;

    private final DeletedUrlPathRepository deletedUrlPathRepository;

    private final MemberRepository memberRepository;

    private final Scalr_Resize_S3Uploader scalr_resize_s3Uploader;

    public ResponseEntity getProfile(Principaldetail principaldetail) {
        Optional<Member> member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId());
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member.orElseThrow(()-> new IllegalArgumentException(ExceptionMsg.NOT_MATCHED_USER_INFO.getMsg())));
        memberResponseDto.setTwoField(GenerateMsg.getMsg(SuccessMsg.GET_PROFILE.getCode(), SuccessMsg.GET_PROFILE.getMsg()));
        return ResponseEntity.ok(memberResponseDto);
    }

    public ResponseEntity deleteToken(Principaldetail principaldetail){
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMsg.ALREADY_LOGGED_OUT.getMsg()));

        UriComponents builder = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/logout")
                .queryParam("client_id", KAKAO_SNS_CLIENT_ID)
                .queryParam("logout_redirect_uri", "http:localhost:8080/delete/user/logout")
                .build();
        try {
            URL url = new URL(builder.toUriString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responsecode = conn.getResponseCode();
            System.out.println(responsecode);
        } catch (MalformedURLException e){
            throw new IllegalArgumentException(ExceptionMsg.INCORRECT_FORM.getMsg());
        } catch (IOException e){
            throw new IllegalArgumentException(ExceptionMsg.DO_IT_PROPERLY.getMsg());
        }
        //(6)
        return ResponseEntity.ok(GenerateMsg.getMsg(SuccessMsg.LOGOUT_SUCCESS.getCode(), SuccessMsg.LOGOUT_SUCCESS.getMsg()));
    }
    public ResponseEntity deleteMember(Principaldetail principaldetail) {
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMsg.ALREADY_LOGGED_OUT.getMsg()));
        memberRepository.findBySocialId(principaldetail.getMember().getSocialId())
                .map(this::deleteDb)
                .orElseThrow(() -> new IllegalArgumentException(ExceptionMsg.ALREADY_CANCELED_MEMBERSHIP.getMsg()));
        return ResponseEntity.ok(GenerateMsg.getMsg(SuccessMsg.CANCEL_MEMBERSHIP.getCode(), SuccessMsg.CANCEL_MEMBERSHIP.getMsg()));
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
            throw new IllegalArgumentException(ExceptionMsg.NO_IMAGE_FILE.getMsg());
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
        imageDto.SetTwoproperties(GenerateMsg.getMsg(SuccessMsg.CHANGE_IMAGE.getCode(), SuccessMsg.CHANGE_IMAGE.getMsg()));
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
            throw new IllegalArgumentException(ExceptionMsg.NO_CONTENTS.getMsg());
        }
        memberRepository.save(member);
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member);
        memberResponseDto.setTwoField(GenerateMsg.getMsg(SuccessMsg.CHANGE_STATUS.getCode(), SuccessMsg.CHANGE_STATUS.getMsg()));
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
