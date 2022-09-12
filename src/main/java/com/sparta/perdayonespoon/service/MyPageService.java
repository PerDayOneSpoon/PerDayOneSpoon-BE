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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final RestTemplate restTemplate;
    @Value("${spring.security.oauth2.client.registration.kakao.clientId}")
    private String KAKAO_SNS_CLIENT_ID;
    private final RefreshTokenRepository refreshTokenRepository;

    private final DeletedUrlPathRepository deletedUrlPathRepository;

    private final MemberRepository memberRepository;

    private final Scalr_Resize_S3Uploader scalr_resize_s3Uploader;

    public ResponseEntity getProfile(Principaldetail principaldetail) {
        Optional<Member> member = memberRepository.findBySocialId(principaldetail.getMember().getSocialId());
        MemberResponseDto memberResponseDto = MemberMapper.INSTANCE.orderToDto(member.orElseThrow(()-> new IllegalArgumentException("유저정보가 일치하지 않습니다.")));
        memberResponseDto.setTwoField(GenerateMsg.getMsg(HttpServletResponse.SC_OK,memberResponseDto.getNickName()+"프로필 조회에 성공하셨습니다."));
        return ResponseEntity.ok(memberResponseDto);
    }

    public ResponseEntity deleteToken(Principaldetail principaldetail){
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));

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
            throw new IllegalArgumentException("형식을 맞춰주세요");
        } catch (IOException e){
            throw new IllegalArgumentException("제대로 해주세요");
        }
        //(6)
        return ResponseEntity.ok(GenerateMsg.getMsg(HttpServletResponse.SC_OK,principaldetail.getMember().getNickName()+"님 로그아웃에 성공하셨습니다."));
    }
    public ResponseEntity deleteMember(Principaldetail principaldetail) {
        refreshTokenRepository.findByKey(principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 로그아웃한 사용자입니다."));
        memberRepository.findBySocialId(principaldetail.getMember().getSocialId())
                .map(this::deleteDb)
                .orElseThrow(() -> new IllegalArgumentException("이미 탈퇴한 회원입니다."));
        return ResponseEntity.ok(GenerateMsg.getMsg(HttpServletResponse.SC_OK,principaldetail.getMember().getNickName()+"님 회원탈퇴 성공하셨습니다."));
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
}
