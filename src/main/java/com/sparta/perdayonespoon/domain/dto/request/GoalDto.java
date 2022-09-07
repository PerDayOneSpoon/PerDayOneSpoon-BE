package com.sparta.perdayonespoon.domain.dto.request;

import lombok.Data;

import javax.annotation.Nullable;

@Data
public class GoalDto {
    public Long id;
    public String title;
    public String Start_date;
    public String End_date;
    public String time;

    public long characterId;
    public long category;
    public boolean privateCheck;
}
