package com.sparta.perdayonespoon.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageDto {
    private String uploadImageUrl;
    private String imageName;

    private String msg;
    private long code;


    @Builder
    public ImageDto(String uploadImageUrl, String imageName) {
        this.uploadImageUrl = uploadImageUrl;
        this.imageName = imageName;
    }
}
