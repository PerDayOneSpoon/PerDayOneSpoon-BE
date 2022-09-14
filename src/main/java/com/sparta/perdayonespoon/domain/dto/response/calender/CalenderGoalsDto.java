package com.sparta.perdayonespoon.domain.dto.response.calender;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CalenderGoalsDto {

    private Long id;

    private int characterId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String currentDate;
    private String time;
    private String charactorColor;
    private boolean privateCheck;
    private boolean achievementCheck;

    @QueryProjection
    public CalenderGoalsDto(Long id,String title, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime currentDate, String time,int characterId,boolean privateCheck,boolean achievementCheck){
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        this.time = time;
        this.characterId = characterId;
        this.charactorColor = GetCharacterUrl.getMandooColor(characterId);
        this.privateCheck = privateCheck;
        this.achievementCheck = achievementCheck;
    }

    public TodayGoalsDto getTodayGoalsDto(){
        return TodayGoalsDto.builder()
                .id(id)
                .title(title)
                .time(time)
                .startDate(startDate)
                .endDate(endDate)
                .currentDate(currentDate)
                .characterUrl(characterId)
                .privateCheck(privateCheck)
                .achievementCheck(achievementCheck)
                .build();
    }
}
