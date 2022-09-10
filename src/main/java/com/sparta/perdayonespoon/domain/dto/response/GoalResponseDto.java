package com.sparta.perdayonespoon.domain.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class GoalResponseDto {

    private Long id;
    private String title;
    private String start_date;
    private String end_date;
    private String time;
    private long CharacterId;
    private long category;
    private boolean privateCheck;

    private boolean achievementCheck;
    private long code;
    private String msg;

    private String socialId;

    @Builder
    public GoalResponseDto(String title, String start_date, String end_date, String time, long characterId,
                           Long id,long category,boolean privateCheck,MsgDto msgDto,String socialId, boolean achievementCheck){
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.time = time;
        this.CharacterId = characterId;
        this.category = category;
        this.privateCheck = privateCheck;
        this.msg = msgDto.getMsg();
        this.code = msgDto.getCode();
        this.socialId = socialId;
        this.achievementCheck = achievementCheck;
        this.id = id;
    }

    public void SetTwoProperties(MsgDto msgDto){
        this.code = msgDto.getCode();
        this.msg = msgDto.getMsg();
    }
}
