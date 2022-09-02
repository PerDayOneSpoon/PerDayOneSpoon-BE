package com.sparta.perdayonespoon.domain.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MsgDto {
    private long code;
    private String msg;
}
