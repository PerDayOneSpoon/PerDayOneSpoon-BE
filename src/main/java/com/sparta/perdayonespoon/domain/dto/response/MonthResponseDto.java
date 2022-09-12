package com.sparta.perdayonespoon.domain.dto.response;

import lombok.Builder;
import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

@Data
@Builder
public class MonthResponseDto {

    private String date;

    private String name;

    private String endDate;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
