package com.sparta.perdayonespoon.domain.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "친구 검색시 바디에 들어가는 내용들")
@Data
public class MemberSearchDto {

    @ApiModelProperty(example = "사용자의 닉네임")
    private String nickName;
    @ApiModelProperty(example = "사용자의 상태 메세지")
    private String status;
    @ApiModelProperty(example = "사용자의 프로필 이미지")
    private String ImgUrl;
    @ApiModelProperty(example = "사용자의 검색 코드")
    private String socialCode;
    @ApiModelProperty(example = "사용자의 이메일")
    private String Email;

    @QueryProjection
    public MemberSearchDto(String nickName, String status, String ImgUrl, String socialCode, String Email){
        this.nickName=nickName;
        this.status=status;
        this.ImgUrl=ImgUrl;
        this.socialCode=socialCode;
        this.Email=Email;
    }
}
