package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.SuccessMsg;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.AchivementResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.EveryTwoDaysGoalDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.PrivateBadgeCheckDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.WeekRateDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import com.sparta.perdayonespoon.util.GetCharacterUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MainService {


    private final MemberRepository memberRepository;

    private final BadgeRepository badgeRepository;
    private static Stack<String> socialst = new Stack<>();
    private static Set<Integer> daylist = new HashSet<>();
    private static Stack<Boolean> goalst = new Stack<>();
    private static double truecount =0;
    private static double totalcount = 0;
    private static long period=0;
    private final GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public ResponseEntity getGoal(Principaldetail principaldetail) {
        LocalDateTime sunday;
        LocalDateTime saturday;
        int day = LocalDate.now().getDayOfWeek().getValue();
        List<GoalRateDto> goalRateDtos;
        if(day != 6 && day != 7) {
            sunday = LocalDateTime.now().minusDays(day);
            saturday = LocalDateTime.now().plusDays(6-day);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream().sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal)).forEach(this::setRate);
        }else if(day == 6){
            sunday = LocalDateTime.now().minusDays(day);
            saturday = LocalDateTime.now();
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream().sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal)).forEach(this::setRate);
        }else {
            sunday = LocalDateTime.now();
            saturday = LocalDateTime.now().plusDays(6);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream().sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal)).forEach(this::setRate);
        }
        if(!socialst.isEmpty() && !goalst.isEmpty()){
            socialst.clear();
            goalst.clear();
        }
        List<WeekRateDto> weekRateDtoList ;
        weekRateDtoList = goalRateDtos.stream().filter(this::checkgoalgetday).map(GoalRateDto::getWeekRateDto).collect(Collectors.toList());
        if(weekRateDtoList.isEmpty())
            weekRateDtoList = new ArrayList<>();
        for(int y=1; y<=7; y++){
            if(!daylist.isEmpty()) {
                if (!daylist.contains(y)) {
                    if(y == 7){
                        weekRateDtoList.add(0,WeekRateDto.builder().id(0).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                    }
                    else weekRateDtoList.add(y-1,WeekRateDto.builder().id(y).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
            }
            else {
                if(y == 7){
                    weekRateDtoList.add(0,WeekRateDto.builder().rate(0).id(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
                else weekRateDtoList.add(WeekRateDto.builder().rate(0).id(y).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());}
        }
        if(!daylist.isEmpty()){
            daylist.clear();
        }
        List<TodayGoalsDto> todayGoalsDtoList = goalRepository.getTodayGoal(LocalDateTime.now(),principaldetail.getMember().getSocialId());
        AchivementResponseDto achivementResponseDto = AchivementResponseDto.builder()
                .weekRateDtoList(weekRateDtoList)
                .todayGoalsDtoList(todayGoalsDtoList)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"주간 습관 확인에 성공하셨습니다. 힘내세요!"))
                .weekStartDate(sunday.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .weekEndDate(saturday.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .currentDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .build();
        return ResponseEntity.ok(achivementResponseDto);
    }
    private boolean checkgoalgetday(GoalRateDto goalRateDto){
        if(goalRateDto.isCheckGoal()){
            daylist.add(goalRateDto.getWhatsDay());
            return true;
        }
        return false;
    }
    //Todo: true false가 다 존재할땐 기능하지만 개별적으로 존재할때 기능이 동작할지 의문?
    private void setRate(GoalRateDto goalRateDto) {
        if (socialst.isEmpty() && goalst.isEmpty()) {
            socialst.push(goalRateDto.getDayString());
            goalst.push(goalRateDto.isCheckGoal());
            totalcount = goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalcount);
                goalRateDto.setRate(Math.round((truecount / totalcount) * 100));
            }
        } else if (socialst.peek().equals(goalRateDto.getDayString()) && goalst.peek() == !goalRateDto.isCheckGoal()) {
            socialst.pop();
            goalst.pop();
            totalcount += goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalcount);
                goalRateDto.setRate(Math.round((truecount / totalcount) * 100));
            }
        } else if (!socialst.peek().equals(goalRateDto.getDayString())) {
            socialst.pop();
            goalst.pop();
            socialst.add(goalRateDto.getDayString());
            goalst.add(goalRateDto.isCheckGoal());
            totalcount = goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalcount);
                goalRateDto.setRate(Math.round((truecount / totalcount) * 100));
            }
        }
    }
    // TODO : 달력 날짜 받기X 주간 달성도 리턴하기
    public ResponseEntity CreateGoal(GoalDto goalDto, Principaldetail principaldetail) {
        int sunday = LocalDateTime.now().getDayOfWeek().getValue();
        String goalFlag = UUID.randomUUID().toString();
        if(goalDto.getTitle() == null) {
            throw new IllegalArgumentException("제목을 입력해주세요");
        } else if (goalDto.getCharacterId() == 0){
            throw new IllegalArgumentException("캐릭터를 선택해 주세요");
        }
        Member member = memberRepository.findByMemberId(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 없습니다."));
        Map<String , Boolean> badgeMap = new HashMap<>();
        if(!member.getBadgeList().isEmpty()) {
            badgeMap.put("welcomeBadge",member.getBadgeList().stream().anyMatch(badge -> badge.getBadgeName().equals("웰컴 뱃지")));
            badgeMap.put("privateBadge",member.getBadgeList().stream().anyMatch(badge -> badge.getBadgeName().equals("프라이빗 벳지")));
            badgeMap.put("comebackBadge",member.getBadgeList().stream().anyMatch(badge -> badge.getBadgeName().equals("컴백 뱃지")));
        }
        int x=0;
        List<Goal> goalList = new ArrayList<>();
        if(checkdate(LocalTime.parse(goalDto.time),goalDto.category,principaldetail.getMember().getSocialId())){
            // 웰컴뱃지 없으면 false 있으면 true
            List<Badge> badgeList = new ArrayList<>();
            if(!badgeMap.containsKey("welcomeBadge")) {
                getWelcomeBadge(member, badgeList);
            }else if(!badgeMap.get("welcomeBadge")) {
                getWelcomeBadge(member, badgeList);
            }
            // 프라이빗 뱃지 없으면 false 있으면 true
            if(!badgeMap.containsKey("privateBadge")) {
                getPrivateBadge(member, badgeList);
            } else if (!badgeMap.get("privateBadge")){
                getPrivateBadge(member, badgeList);
            }
            // 일요일 체크 / 월.일로 체크해서 퐁당퐁당 뱃지 지급
            if(sunday == 7){
                if(member.getBadgeList().stream().noneMatch(badge->badge.getBadgeName().equals("퐁당 퐁당 뱃지"))){
                    LocalDate monday = LocalDate.now().minusDays(sunday-1);
                    List<EveryTwoDaysGoalDto> everyTwoDaysGoalDtoList = goalRepository.getTheseWeeksGoals(monday,LocalDate.now(),principaldetail.getMember().getSocialId());
                    if(everyTwoDaysGoalDtoList.size() == 4) {
                        if (4 == everyTwoDaysGoalDtoList.stream().map(EveryTwoDaysGoalDto::getCurrentDay).filter(d -> d == 1 || d == 3 || d == 5 || d == 7).count()) {
                            badgeList.add(Badge
                                    .builder()
                                    .badgeName("퐁당 퐁당 뱃지")
                                    .member(member)
                                    .createdAt(LocalDateTime.now().toLocalDate())
                                    .badgeNumber(3)
                                    .build());
                        }
                    }
                }
            }
            // 컴백 뱃지 없으면 false 있으면 true
            if(!badgeMap.containsKey("comebackBadge")) {
                if(!member.getGoalList().isEmpty()) {
                    getComebackBadge(member, badgeList);
                }
            }else if(!badgeMap.get("comebackBadge")){
                getComebackBadge(member, badgeList);
            }
            if(member.getBadgeList().size()>=5){
                if(member.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("뱃지 왕 뱃지"))){
                    badgeList.add(Badge
                            .builder()
                            .badgeName("뱃지 왕 뱃지")
                            .member(member)
                            .createdAt(LocalDateTime.now().toLocalDate())
                            .badgeNumber(5)
                            .build());}
            }
            if(!badgeList.isEmpty()) {
                badgeRepository.saveAll(badgeList);
            }

            while(period -->0){
                    goalList.add(Goal.builder()
                    .achievementCheck(goalDto.achievementCheck)
                    .startDate(LocalDateTime.now())
                    .currentDate(LocalDateTime.now().plusDays(x))
                    .endDate(LocalDateTime.now().plusDays(goalDto.category-1))
                    .privateCheck(goalDto.privateCheck)
                    .socialId(principaldetail.getMember().getSocialId())
                    .time(goalDto.time + ":00")
                    .category(goalDto.category)
                    .characterId(goalDto.characterId)
                    .title(goalDto.title)
                    .goalFlag(goalFlag)
                    .member(member)
                    .build());
                x++;
            }
            goalRepository.saveAll(goalList);
            List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
            goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                    .currentdate(Goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                    .achievementCheck(Goal.isAchievementCheck())
                    .id(Goal.getId())
                    .socialId(Goal.getSocialId())
                    .title(Goal.getTitle())
                    .characterUrl(GetCharacterUrl.getMandooUrl(Goal.getCharacterId()))
                    .endDate(Goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                    .startDate(Goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                    .privateCheck(Goal.isPrivateCheck())
                    .time(Goal.getTime())
                    .goalFlag(Goal.getGoalFlag())
                    .msgDto(GenerateMsg.getMsg(SuccessMsg.CREATE_GOALS.getCode(), SuccessMsg.CREATE_GOALS.getMsg()))
                    .build()));
            return ResponseEntity.ok(goalResponseDtoList);
        }
            throw new IllegalArgumentException("하루에 최대 5개까지만 습관 생성이 가능합니다.");
    }

    private void getWelcomeBadge(Member member, List<Badge> badgeList) {
        badgeList.add(Badge
                .builder()
                .badgeName("웰컴 뱃지")
                .member(member)
                .createdAt(LocalDateTime.now().toLocalDate())
                .badgeNumber(1)
                .build());
    }

    private void getPrivateBadge(Member member, List<Badge> badgeList) {
        List<String> privateBadgeCheckDtoList = member.getGoalList().stream().filter(Goal::isPrivateCheck).map(Goal::getGoalFlag).distinct().collect(Collectors.toList());
        if (privateBadgeCheckDtoList.size() >= 10) {
            badgeList.add(Badge
                    .builder()
                    .badgeName("프라이빗 뱃지")
                    .member(member)
                    .createdAt(LocalDateTime.now().toLocalDate())
                    .badgeNumber(2)
                    .build());
        }
    }

    private void getComebackBadge(Member member, List<Badge> badgeList) {
        LocalDate latestDay = member.getGoalList().stream().sorted(Comparator.comparing(Goal::getCurrentDate).reversed()).map(Goal::getCurrentDate).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("목표가 존재하지 않습니다.")).toLocalDate();
//               LocalDate latestDay = goalRepository.getLatestGoals(principaldetail.getMember().getSocialId()).toLocalDate();
        LocalDate today = LocalDate.now();
        Period pe = Period.between(latestDay, today);
        if (pe.getDays() >= 7) {
            badgeList.add(Badge
                    .builder()
                    .badgeName("컴백 뱃지")
                    .member(member)
                    .createdAt(LocalDateTime.now().toLocalDate())
                    .badgeNumber(4)
                    .build());
        }
    }
    private boolean checkdate (LocalTime time, long category,String socialId){
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime endDate = localDateTime.plusDays(category);
        period = Period.between(localDateTime.toLocalDate(),endDate.toLocalDate()).getDays();
        Optional<CountDto> countDto = goalRepository.getCountGoal(localDateTime,socialId);
        if(countDto.isPresent()) {
            if (countDto.get().getTotalCount() >= 5) {
                throw new IllegalArgumentException("하루의 습관은 최대 5개까지만 가능합니다. 다시 확인해주세요");
            }
        }
        if(time.getHour() == 0 && time.getMinute() == 0){
            throw new IllegalArgumentException("설정한 습관의 타이머를 유효한 값으로 수정해주세요");
        }
        if(localDateTime.getDayOfMonth() == localDateTime.plusHours(time.getHour()).getDayOfMonth()) {
            LocalDateTime localDateTime1 = localDateTime.plusHours(time.getHour());
            if (localDateTime.getDayOfMonth() == localDateTime1.plusMinutes(time.getMinute()).getDayOfMonth()) {
                return true;
            } else
                throw new IllegalArgumentException("금일을 넘는 목표는 생성할 수 없습니다. 다시 생성해 주세요");
        }
        else
            throw new IllegalArgumentException("금일을 넘는 목표는 생성할 수 없습니다. 다시 생성해 주세요");
    }

    public ResponseEntity<GoalResponseDto> ChangeGoal(long goalId,Boolean achievement,Principaldetail principaldetail) {
        if(achievement == null){
            throw new IllegalArgumentException("통신시 달성여부가 보내져야 합니다.");
        }
        Goal goal = goalRepository.findOldGoal(goalId,principaldetail.getMember().getSocialId())
                .map(g -> changeCheckGoal(g,achievement))
                .orElseThrow(() -> new IllegalArgumentException("해당 습관이 존재하지 않습니다."));
        CheckAndGiveBadge(goal);

        return ResponseEntity.ok().body(GoalResponseDto.builder()
                .currentdate(goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .startDate(goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .endDate(goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .title(goal.getTitle())
                .achievementCheck(goal.isAchievementCheck())
                .id(goal.getId())
                .privateCheck(goal.isPrivateCheck())
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"습관 달성 축하드립니다.!!! 고생 많으셨어요"))
                .socialId(goal.getSocialId())
                .characterId(goal.getCharacterId())
                .time(goal.getTime())
                .build());
    }

    private Goal changeCheckGoal(Goal goal,boolean achievement) {
        goal.SetAchivementCheck(achievement);
        return goalRepository.save(goal);
    }

    private void CheckAndGiveBadge(Goal goal) {
        List<Badge> badgeList = new ArrayList<>();
        Map<String, Boolean> badgeMap = new HashMap<>();
        if(!goal.getMember().getBadgeList().isEmpty()){
            badgeMap.put("earlyBirdBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("얼리버드 뱃지")));
            badgeMap.put("owlBirdBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("올빼미 뱃지")));
            badgeMap.put("shortTimeBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("단타 뱃지")));
            badgeMap.put("longTimeBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("장타 뱃지")));
            badgeMap.put("minBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("미니멈 뱃지")));
            badgeMap.put("maxBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("맥시멈 뱃지")));
            badgeMap.put("shortWayBadge", goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("단거리 뱃지")));
            badgeMap.put("middleWayBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("중거리 뱃지")));
            badgeMap.put("longWayBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("장거리 뱃지")));
        }
        LocalTime earlyStart = LocalTime.of(4, 59, 59);
        LocalTime earlyEnd = LocalTime.of(7, 0, 1);
        LocalTime dawnStart = LocalTime.of(10,59,59);
        LocalTime dawnEnd = LocalTime.of(2,0,1);
        LocalTime shortTime = LocalTime.of(0,3,1);
        LocalTime longTime = LocalTime.of(4,0,1);
        if(!badgeMap.containsKey("earlyBirdBadge")){
            earlyMorningBadge(goal, badgeList, earlyStart, earlyEnd);
        }else if (!badgeMap.get("earlyBirdBadge")){
            earlyMorningBadge(goal, badgeList, earlyStart, earlyEnd);
        }
        if(!badgeMap.containsKey("owlBirdBadge")){
            owlBadge(goal, badgeList, dawnStart, dawnEnd);
        }else if (!badgeMap.get("owlBirdBadge")){
            owlBadge(goal, badgeList, dawnStart, dawnEnd);
        }
        if(!badgeMap.containsKey("shortTimeBadge")){
            shortBadge(goal, badgeList, shortTime);
        }else if (!badgeMap.get("shortTimeBadge")){
            shortBadge(goal, badgeList, shortTime);
        }
        if(!badgeMap.containsKey("longTimeBadge")){
            longBadge(goal, badgeList, longTime);
        }else if (!badgeMap.get("longTimeBadge")){
            longBadge(goal, badgeList, longTime);
        }
        if(!badgeList.isEmpty())
            badgeRepository.saveAll(badgeList);
    }
    private void earlyMorningBadge(Goal goal, List<Badge> badgeList, LocalTime earlyStart, LocalTime earlyEnd) {
        if(LocalTime.now().isAfter(earlyStart) && LocalTime.now().isBefore(earlyEnd)){
            badgeList.add(Badge.builder()
                    .badgeNumber(9)
                    .badgeName("얼리 버드 뱃지")
                    .member(goal.getMember())
                    .createdAt(LocalDateTime.now().toLocalDate())
                    .build());
        }
    }

    private void owlBadge(Goal goal, List<Badge> badgeList, LocalTime dawnStart, LocalTime dawnEnd) {
        if(LocalTime.now().isAfter(dawnStart) && LocalTime.now().isBefore(dawnEnd)){
            badgeList.add(Badge.builder()
                    .badgeName("올빼미 뱃지")
                    .badgeNumber(10)
                    .member(goal.getMember())
                    .createdAt(LocalDateTime.now().toLocalDate())
                    .build());
        }
    }

    private void shortBadge(Goal goal, List<Badge> badgeList, LocalTime shortTime) {
        if(LocalTime.parse(goal.getTime()).isBefore(shortTime)){
            badgeList.add(Badge.builder()
                    .member(goal.getMember())
                    .badgeName("단타 뱃지")
                    .badgeNumber(13)
                    .createdAt(LocalDateTime.now().toLocalDate())
                    .build());
        }
    }

    private void longBadge(Goal goal, List<Badge> badgeList, LocalTime longTime) {
        if(LocalTime.parse(goal.getTime()).isAfter(longTime)){
            badgeList.add(Badge.builder()
                    .badgeNumber(14)
                    .badgeName("장타 뱃지")
                    .member(goal.getMember())
                    .createdAt(LocalDateTime.now().toLocalDate())
                    .build());
        }
    }


    public ResponseEntity deleteGoals(Principaldetail principaldetail, String deleteFlag) {
        List<Goal> goalList = goalRepository.getCategoryGoals(principaldetail.getMember().getSocialId(),deleteFlag);
        int category;
        if(goalList.isEmpty()){
            throw new IllegalArgumentException("해당 습관이 존재하지 않습니다.");
        }
        else {
            category = goalList.get(0).getCategory();
            goalRepository.deleteAll(goalList);
        }
        return ResponseEntity.ok().body(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"만드셨던"+ category +"일치의 습관을 모두 삭제하셨습니다."));
    }
}