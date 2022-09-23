package com.sparta.perdayonespoon.domain.dto.request;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class StatusDto {
    @Nullable
    private String nickname;
    @Nullable
    private String status;
}
