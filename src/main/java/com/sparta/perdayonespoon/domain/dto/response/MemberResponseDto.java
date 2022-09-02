package com.sparta.perdayonespoon.domain.dto.response;

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
@Builder
@ApiModel(description = "소셜 로그인시 반환되는 바디에 들어갈 내용들")
public class MemberResponseDto {
    @ApiModelProperty(example = "소셜 로그인시 저장된 db 번호")
    private Long id;

    @ApiModelProperty(example = "소셜 로그인시 가져온 email")
    private String socialEmail;

    @ApiModelProperty(example = "소셜 로그인시 가져온 nickname")
    private String nickname;

    @ApiModelProperty(example = "소셜 로그인시 가져온 socialId")
    private String socialId;

    @ApiModelProperty(example = "소셜 로그인시 생성한 socialCode")
    private String socialCode;

    @ApiModelProperty(example = "소셜 로그인시 가져온 profileImage")
    private String profileImage;

    @ApiModelProperty(example = "소셜 로그인시 가져온 권한")
    private Authority authority;

    @ApiModelProperty(example = "통신 성공시 서버에서 보내주는 상태code")
    private long code;


    @ApiModelProperty(example = "통신 성공시 서버에서 보내주는 응답 메세지")
    private String msg;

    @ApiIgnore
    public void setTwoField(MsgDto msgDto){
        code = msgDto.getCode();
        msg = msgDto.getMsg();
    }
}
