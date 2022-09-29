package com.sparta.perdayonespoon.util;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.Goal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class BadgeUtil {

    public static void longBadge(Goal goal, List<Badge> badgeList, LocalTime longTime) {
        if(LocalTime.parse(goal.getTime()).isAfter(longTime)){
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeNumber(14)
                    .badgeName("장타 뱃지")
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    public static Badge plopBadge(Goal goal) {
        int plopPoint =0;
        if(goal.getCategory() == 3){
            List<Goal>goalList = goal.getMember().getGoalList()
                    .stream()
                    .filter(g->g.getGoalFlag().equals(goal.getGoalFlag()))
                    .collect(Collectors.toList());
            for(int i=0; i<3; i++){
                if(goalList.get(1).isAchievementCheck()) {
                    plopPoint = 0;
                    break;
                }
                else if (goalList.get(i).isAchievementCheck()){
                    plopPoint++;
                }
            }
            if(plopPoint == 2){
                return makePlopBadge(goal);
            }
        } else if(goal.getCategory() == 7){
            List<Goal> goalList = goal.getMember().getGoalList()
                    .stream()
                    .filter(g->g.getGoalFlag().equals(goal.getGoalFlag()))
                    .collect(Collectors.toList());
            if(goalList.get(0).isAchievementCheck()) {
                plopPoint++;
                for(int i=1; i<7; i++){
                    if(i%2 == 0){
                        if (goalList.get(i).isAchievementCheck()){
                            plopPoint++;
                        }
                        else break;
                    }else {
                        if(goalList.get(i).isAchievementCheck()) {
                            break;
                        }
                    }
                }
                if(plopPoint == 4){
                    return makePlopBadge(goal);
                }
            }else{
                for(int i=1; i<7; i++){
                    if(i%2 == 1){
                        if(goalList.get(i).isAchievementCheck()){
                            plopPoint++;
                        }
                        else
                            break;
                    }
                    else {
                        if(goalList.get(i).isAchievementCheck()) break;
                    }
                }
                if(plopPoint == 3){
                    return makePlopBadge(goal);
                }
            }
        }
        return Badge.fakeBadgeBuilder().badgeName("가짜 뱃지").build();
    }

    private static Badge makePlopBadge(Goal goal) {
        return Badge.realBadgeBuilder()
                .badgeNumber(3)
                .badgeName("퐁당 퐁당 뱃지")
                .member(goal.getMember())
                .createdAt(LocalDate.now())
                .build();
    }

    public static String getBadgeExplain(int badgeNumber){
        switch (badgeNumber){
            case 1: return "처음으로 습관을 만들면 받을 수 있을지도 몰라요";
            case 2: return "비밀이 많아지다 보면 받을 수 있을지도 몰라요";
            case 3: return "띄엄띄엄 습관을 달성하다보면 받을 수 있을지도 몰라요";
            case 4: return "습관을 만들고 떠나버린 당신! 지금 당장 만들면 받을 수 있을지도 몰라요";
            case 5: return "뱃지를 수집하다보면 얻을 수 있을지도 몰라요";
            case 6: return "친구가 많아지면 얻을 수 있을지도 몰라요";
            case 7: return "일찍 일어나는 새가 뱃지를 얻는다! 당신에게도 비슷한 일이 일어날지 몰라요";
            case 8: return "올빼미처럼 야간에 활동하는 사람은 얻을 수 있을지도 몰라요";
            case 9: return "친구들의 습관에 좋아요를 눌러봐요! 예쁜 뱃지가 찾아올지 몰라요";
            case 10: return "많은 친구들에게 좋아요를 받을 수 있는 습관을 만들어 보아요";
            case 11: return "10일간 꾸준히!! 작심삼일을 극복해보세요";
            case 12: return "20일간 꾸준히!! 조금만 더 노력해서 완벽한 습관을 만들어 보아요";
            case 13: return "30일간 꾸준히!! 한달 전과는 다른 당신의 모습을 발견할 수 있을거에요";
            case 14: return "일주일간 하나의 습관이라도 달성해 보아요";
            case 15: return "일주일간 습관을 최대한 많이 만들고 달성해 당신의 한계를 도전해보세요";
            case 16: return "짧은 시간 습관을 달성하면 얻을 수 있을지도 몰라요";
            case 17: return "오랜 시간 습관을 달성하면 얻을 수 있을지도 몰라요";
            default: return "";
        }
    }

    public static String getBadgeUrl(int badgeNumber){
        switch (badgeNumber){
            case 1: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/1.png";
            case 2: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/2.png";
            case 3: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/3.png";
            case 4: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/4.png";
            case 5: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/5.png";
            case 6: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/6.png";
            case 7: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/7.png";
            case 8: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/8.png";
            case 9: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/9.png";
            case 10: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/10.png";
            case 11: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/11.png";
            case 12: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/12.png";
            case 13: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/13.png";
            case 14: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/14.png";
            case 15: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/15.png";
            case 16: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/16.png";
            case 17: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/17.png";
            case 18: return "https://perday-onespoon.s3.ap-northeast-2.amazonaws.com/badge/18.png";
            default: return "";
        }
    }

    public static String getBadgeName(int badgeNumber){
        switch (badgeNumber){
            case 1: return "웰컴 뱃지";
            case 2: return "프라이빗 뱃지";
            case 3: return "퐁당 퐁당 뱃지";
            case 4: return "컴백 뱃지";
            case 5: return "뱃지 왕 뱃지";
            case 6: return "인싸 뱃지";
            case 7: return "얼리버드 뱃지";
            case 8: return "올빼미 뱃지";
            case 9: return "긍정 뱃지";
            case 10: return "매력 뱃지";
            case 11: return "단거리 뱃지";
            case 12: return "중거리 뱃지";
            case 13: return "장거리 뱃지";
            case 14: return "미니멈 뱃지";
            case 15: return "맥시멈 뱃지";
            case 16: return "단타 뱃지";
            case 17: return "장타 뱃지";
            default: return "";
        }
    }
}
