package com.sparta.perdayonespoon.domain.dto.response.badge;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BadgeResponseDto {

    private String badgeName;

    private long badgeNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일")
    private LocalDate createdAt;

    private String badgeUrl;

    private String badgeInfo;

    private boolean hasBadge;

    @Builder(builderClassName = "memberBadge" , builderMethodName = "memberBadgeBuilder")
    public BadgeResponseDto(String badgeName, long badgeNumber,LocalDate createdAt, String badgeInfo, String badgeUrl){
        this.badgeName = badgeName;
        this.badgeNumber =badgeNumber;
        this.createdAt = createdAt;
        this.badgeInfo = badgeInfo;
        this.badgeUrl = badgeUrl;
        this.hasBadge = true;
    }
    @Builder(builderClassName = "formBadge" , builderMethodName = "formBadgeBuilder")
    public BadgeResponseDto(String badgeName, int badgeNumber, String badgeInfo, String badgeUrl){
        this.badgeName = badgeName;
        this.badgeNumber =badgeNumber;
        this.badgeInfo = badgeInfo;
        this.badgeUrl = badgeUrl;
        this.hasBadge = false;
    }
}
