package com.sparta.perdayonespoon.domain.dto;

import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private String uploadImageUrl;
    private String imageName;

    private String Msg;
    private long code;


    @Builder
    public ImageDto(String uploadImageUrl, String imageName) {
        this.uploadImageUrl = uploadImageUrl;
        this.imageName = imageName;
    }

    public void SetTwoproperties(MsgDto msgDto){
        this.Msg = msgDto.getMsg();
        this.code = msgDto.getCode();
    }
}
