package com.sparta.perdayonespoon.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "member")
@Entity // RDS
@ApiModel
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(example = "소셜 로그인한 사용자의 이메일 , 없을수도 있음")
    @Column
    private String Email;

    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String password;

    @ApiModelProperty(example = "소셜 로그인에서 사용하는 유저의 닉네임")
    @Column(nullable = false)
    private String nickname;

    @ApiModelProperty(example = "소셜로그인시 발급되는 소셜 ID")
    @Column(nullable = false, unique = true)
    private String socialId;

    @ApiModelProperty(example = "유저를 구분하기 위해 생성한 소셜 코드")
    private String socialCode;

    @ApiModelProperty(example = "소셜로그인으로 가입한 사용자의 프로필 사진 경로")
    @Column
    private String profileImage;

    @ApiModelProperty(example = "회원가입한 사용자의 권한")
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String email, String password,String socialCode, String nickname, String socialId,String profileImage, Authority authority) {
        this.Email = email;
        this.password = password;
        this.socialCode = socialCode;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.socialId = socialId;
        this.authority = authority;
    }
}

