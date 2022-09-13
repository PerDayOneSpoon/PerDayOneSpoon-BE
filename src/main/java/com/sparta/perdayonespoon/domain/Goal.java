package com.sparta.perdayonespoon.domain;

import com.sparta.perdayonespoon.domain.dto.response.rate.WeekRateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Goal{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;
    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime currentDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private String time;

    @Column
    private int category;

    @Column
    private int characterId;

    @Column
    private boolean privateCheck;

    @Column
    private String socialId;

    @Column
    private boolean achievementCheck;

    @Builder
    public Goal(String title, LocalDateTime startDate,LocalDateTime currentDate,LocalDateTime endDate, String time, int category,
                int characterId, boolean privateCheck, String socialId,boolean achievementCheck){
        this.title = title;
        this.category = category;
        this.startDate = startDate;
        this.currentDate = currentDate;
        this.endDate = endDate;
        this.characterId = characterId;
        this.privateCheck = privateCheck;
        this.time=time;
        this.socialId=socialId;
        this.achievementCheck=achievementCheck;
    }

    public void SetAchivementCheck(boolean achievementCheck){
        this.achievementCheck = achievementCheck;
    }
}
