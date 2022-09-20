package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "친구 검색시 바디에 들어가는 내용들")
@Data
public class MemberSearchDto {

    @ApiModelProperty(example = "사용자의 DB번호")
    private Long id;
    @ApiModelProperty(example = "사용자의 닉네임")
    private String nickname;
    @ApiModelProperty(example = "사용자의 상태 메세지")
    private String status;
    @ApiModelProperty(example = "사용자의 프로필 이미지")
    private String profileImage;
    @ApiModelProperty(example = "사용자의 검색 코드")
    private String socialCode;

    @ApiModelProperty(example = "사용자에게 발급된 소셜Id")
    private String socialId;

    @ApiModelProperty(example = "사용자의 이메일")
    private String Email;

    @ApiModelProperty(example = "팔로우 했는지 여부")
    private boolean followCheck;

    @ApiModelProperty(example = "본인 여부 확인")
    private boolean selfCheck;
    @QueryProjection
    public MemberSearchDto(String nickname, String status, String profileImage, String socialCode, String Email, String socialId,Long id, boolean followCheck,boolean selfCheck){
        this.nickname=nickname;
        this.status=status;
        this.profileImage=profileImage;
        this.socialCode=socialCode;
        this.Email=Email;
        this.socialId=socialId;
        this.id=id;
        this.followCheck=followCheck;
        this.selfCheck=selfCheck;
    }
}
