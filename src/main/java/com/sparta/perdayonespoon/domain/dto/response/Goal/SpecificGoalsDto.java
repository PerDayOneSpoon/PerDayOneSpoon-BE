package com.sparta.perdayonespoon.domain.dto.response.Goal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.perdayonespoon.comment.domain.entity.Comment;
import com.sparta.perdayonespoon.comment.dto.CommentResponseDto;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SpecificGoalsDto {

    private Long id;
    private String title;
    private String startDate;
    private String endDate;
    private String currentDate;
    private String time;
    private String characterUrl;
    private boolean privateCheck;
    private boolean achievementCheck;
    private int heartCnt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String goalFlag;
    private boolean heartCheck;

    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    @Builder(builderClassName = "MyGoalsBuilder", builderMethodName = "MyGoalsBuilder")
    public SpecificGoalsDto(Goal goal,String nickname){
        id = goal.getId();
        title = goal.getTitle();
        startDate = goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        endDate = goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        currentDate = goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        time = goal.getTime();
        characterUrl = GetCharacterUrl.getMandooUrl(goal.getCharacterId());
        privateCheck = goal.isPrivateCheck();
        achievementCheck = goal.isAchievementCheck();
        heartCnt = goal.getHeartList().size();
        goalFlag =goal.getGoalFlag();
        heartCheck = true;
        goal.getCommentList().forEach(c->convertDto(c,nickname));
    }

    @Builder(builderClassName = "FriendGoalsBuilder", builderMethodName = "FriendGoalsBuilder")
    public SpecificGoalsDto(Goal goal, String socialId,String nickname){
        id = goal.getId();
        title = goal.getTitle();
        startDate = goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        endDate = goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        currentDate = goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13);
        time = goal.getTime();
        characterUrl = GetCharacterUrl.getMandooUrl(goal.getCharacterId());
        privateCheck = goal.isPrivateCheck();
        achievementCheck = goal.isAchievementCheck();
        heartCnt = goal.getHeartList().size();
        goalFlag =goal.getGoalFlag();
        heartCheck = goal.getHeartList().stream().anyMatch(h->h.getSocialId().equals(socialId));
        goal.getCommentList().forEach(c->convertDto(c,nickname));
    }

    private void convertDto(Comment comment,String nickname) {
        boolean isMe = comment.getNickname().equals(nickname);
        commentResponseDtoList.add(CommentResponseDto.builder()
                .goalId(id)
                .isMe(isMe)
                .createdAt(convertTime(comment.getCreatedAt()))
                .profileImage(comment.getProfileImage())
                .commentId(comment.getId())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .build());
    }

    private String convertTime(LocalDateTime localDateTime) {
        int SEC = 60;
        int MIN = 60;
        int HOUR = 24;
        int DAY = 30;
        int MONTH = 12;
        LocalDateTime now = LocalDateTime.now();

        long diffTime = localDateTime.until(now, ChronoUnit.SECONDS); // now보다 이후면 +, 전이면 -

        if (diffTime < SEC) {
            return diffTime + "초전";
        }
        diffTime = diffTime / SEC;
        if (diffTime < MIN) {
            return diffTime + "분 전";
        }
        diffTime = diffTime / MIN;
        if (diffTime < HOUR) {
            return diffTime + "시간 전";
        }
        diffTime = diffTime / HOUR;
        if (diffTime < DAY) {
            return diffTime + "일 전";
        }
        diffTime = diffTime / DAY;
        if (diffTime < MONTH) {
            return diffTime + "개월 전";
        }

        diffTime = diffTime / MONTH;
        return diffTime + "년 전";
    }
}
