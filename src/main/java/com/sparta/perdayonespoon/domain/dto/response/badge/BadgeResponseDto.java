package com.sparta.perdayonespoon.domain.dto.response.badge;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.perdayonespoon.util.BadgeUtil;
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
    public BadgeResponseDto(String badgeName, long badgeNumber,LocalDate createdAt){
        this.badgeName = badgeName;
        this.badgeNumber =badgeNumber;
        this.createdAt = createdAt;
        badgeInfo = BadgeUtil.getBadgeExplain((int) badgeNumber);
        badgeUrl = BadgeUtil.getBadgeUrl((int) badgeNumber);
        this.hasBadge = true;
    }
    @Builder(builderClassName = "formBadge" , builderMethodName = "formBadgeBuilder")
    public BadgeResponseDto(int badgeNumber){
        this.badgeName = BadgeUtil.getBadgeName(badgeNumber);
        this.badgeNumber =badgeNumber;
        badgeInfo = BadgeUtil.getBadgeExplain(badgeNumber);
        badgeUrl = BadgeUtil.getBadgeUrl(18);
        this.hasBadge = false;
    }
}
