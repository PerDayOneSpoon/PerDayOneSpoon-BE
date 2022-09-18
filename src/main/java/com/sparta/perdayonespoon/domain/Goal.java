package com.sparta.perdayonespoon.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="goal")
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

    @Column
    private String goalFlag;

    @OneToMany(mappedBy = "goal",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Heart> heartList = new ArrayList<>();

    @Builder
    public Goal(String title, LocalDateTime startDate,LocalDateTime currentDate,LocalDateTime endDate, String time, int category,
                int characterId, boolean privateCheck, String socialId,boolean achievementCheck,String goalFlag){
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
        this.goalFlag = goalFlag;
    }

    public void SetAchivementCheck(boolean achievementCheck){
        this.achievementCheck = achievementCheck;
    }

    public void SetPrivateCheck(boolean privateCheck){this.privateCheck = privateCheck;}
}
