package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import lombok.Builder;
import lombok.Data;

@Data
public class GoalResponseDto {
    private Long id;
    private String title;
    private String startDate;
    private String endDate;
    private String currentdate;
    private String time;
    private long CharacterId;
    private boolean privateCheck;
    private boolean achievementCheck;
    private long code;
    private String msg;
    private String socialId;

    private String characterUrl;
    @Builder
    public GoalResponseDto(String title, String startDate, String endDate, String time, long characterId,
                           Long id, boolean privateCheck, MsgDto msgDto, String socialId,
                           String currentdate, String characterUrl,boolean achievementCheck){
        this.title = title;
        this.startDate = startDate;
        this.currentdate = currentdate;
        this.endDate = endDate;
        this.time = time;
        this.privateCheck = privateCheck;
        this.characterUrl=characterUrl;
        this.msg = msgDto.getMsg();
        this.code = msgDto.getCode();
        this.socialId = socialId;
        this.achievementCheck = achievementCheck;
        this.id = id;
    }

    @QueryProjection
    public GoalResponseDto(String title, String startDate, String endDate, String time, long characterId,
                           Long id, boolean privateCheck, String socialId, String currentdate, boolean achievementCheck){
        this.title=title;
        this.startDate=startDate;
        this.endDate=endDate;
        this.time=time;
        this.CharacterId=characterId;
        this.id=id;
        this.privateCheck=privateCheck;
        this.socialId = socialId;
        this.currentdate=currentdate;
        this.achievementCheck=achievementCheck;
    }
}
