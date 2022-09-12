package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.Authority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@Setter
@Getter
@ApiModel(description = "소셜 로그인시 반환되는 바디에 들어갈 내용들")
public class MemberResponseDto {
    @ApiModelProperty(example = "소셜 로그인시 저장된 db 번호")
    private Long id;

    @ApiModelProperty(example = "소셜 로그인시 가져온 email")
    private String socialEmail;

    @ApiModelProperty(example = "소셜 로그인시 가져온 nickname")
    private String nickName;

    @ApiModelProperty(example = "소셜 로그인시 가져온 socialId")
    private String socialId;

    @ApiModelProperty(example = "소셜 로그인시 생성한 socialCode")
    private String socialCode;

    @ApiModelProperty(example = "소셜 로그인시 가져온 권한")
    private Authority authority;

    @ApiModelProperty(example = "사용자가 설정한 상태메세지")
    private String status;

    @ApiModelProperty(example = "통신 성공시 서버에서 보내주는 상태code")
    private long code;

    @ApiModelProperty(example = "통신 성공시 서버에서 보내주는 응답 메세지")
    private String msg;

    @ApiModelProperty(example = "소셜 로그인시 가져온 profileImage")
    private String profileImage;

    @ApiIgnore
    public void setTwoField(MsgDto msgDto){
        code = msgDto.getCode();
        msg = msgDto.getMsg();
    }

    @Builder
    public MemberResponseDto(Long id, String socialEmail,String nickName, String socialId, String socialCode, Authority authority
    ,String status, String profileImage){
        this.id = id;
        this.socialEmail = socialEmail;
        this.nickName = nickName;
        this.socialId=socialId;
        this.socialCode=socialCode;
        this.status=status;
        this.authority=authority;
        this.profileImage=profileImage;
    }
}
