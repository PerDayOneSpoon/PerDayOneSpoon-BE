package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.*;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.request.GoalDto;
import com.sparta.perdayonespoon.domain.dto.response.AchivementResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.GoalResponseDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.WeekRateDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.GoalRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.sse.NotificationType;
import com.sparta.perdayonespoon.sse.service.NotificationService;
import com.sparta.perdayonespoon.util.BadgeUtil;
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

    private final NotificationService notificationService;
    private final BadgeUtil badgeUtil;
    private final MemberRepository memberRepository;

    private final BadgeRepository badgeRepository;
    private static double totalCount = 0;
    private final GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public ResponseEntity getGoal(Principaldetail principaldetail) {
        LocalDateTime sunday;
        LocalDateTime saturday;
        Queue<String> socialSt = new LinkedList<>();
        Queue<Boolean> goalSt = new LinkedList<>();
        int day = LocalDate.now().getDayOfWeek().getValue();
        Set<Integer> dayList = new HashSet<>();
        List<GoalRateDto> goalRateDtos;
        if(day != 6 && day != 7) {
            sunday = LocalDateTime.now().minusDays(day);
            saturday = LocalDateTime.now().plusDays(6-day);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream()
                    .sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal))
                    .forEach(g->setRate(g,socialSt,goalSt));
        }else if(day == 6){
            sunday = LocalDateTime.now().minusDays(day);
            saturday = LocalDateTime.now();
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream()
                    .sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal))
                    .forEach(g->setRate(g,socialSt,goalSt));
        }else {
            sunday = LocalDateTime.now();
            saturday = LocalDateTime.now().plusDays(6);
            goalRateDtos = goalRepository.getRateGoal(sunday,saturday,principaldetail.getMember().getSocialId());
            goalRateDtos.stream()
                    .sorted(Comparator.comparing(GoalRateDto::getDayString).thenComparing(GoalRateDto::isCheckGoal))
                    .forEach(g->setRate(g,socialSt,goalSt));
        }
        if(!socialSt.isEmpty() && !goalSt.isEmpty()){
            socialSt.clear();
            goalSt.clear();
        }
        List<WeekRateDto> weekRateDtoList ;
        weekRateDtoList = goalRateDtos.stream()
                .filter(W->checkgoalgetday(W,dayList))
                .map(GoalRateDto::getWeekRateDto)
                .collect(Collectors.toList());
        if(weekRateDtoList.isEmpty())
            weekRateDtoList = new ArrayList<>();
        for(int y=1; y<=7; y++){
            if(!dayList.isEmpty()) {
                if (!dayList.contains(y)) {
                    if(y == 7){
                        weekRateDtoList.add(0,WeekRateDto.builder().id(0).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                    }
                    else weekRateDtoList.add(y,WeekRateDto.builder().id(y).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
            }
            else {
                if(y == 7){
                    weekRateDtoList.add(0,WeekRateDto.builder().rate(0).id(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
                else weekRateDtoList.add(WeekRateDto.builder().rate(0).id(y).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());}
        }
        if(!dayList.isEmpty()){
            dayList.clear();
        }
        List<TodayGoalsDto> todayGoalsDtoList = goalRepository.getTodayGoal(LocalDateTime.now(),principaldetail.getMember().getSocialId());
        AchivementResponseDto achivementResponseDto = AchivementResponseDto.builder()
                .weekRateDtoList(weekRateDtoList)
                .todayGoalsDtoList(todayGoalsDtoList)
                .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("주간 습관 확인에 성공하셨습니다. 힘내세요!").build())
                .weekStartDate(sunday.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .weekEndDate(saturday.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .currentDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .build();
        return ResponseEntity.ok(achivementResponseDto);
    }
    private boolean checkgoalgetday(GoalRateDto goalRateDto, Set<Integer> dayList){
        if(goalRateDto.isCheckGoal()){
            dayList.add(goalRateDto.getWhatsDay());
            return true;
        }
        return false;
    }
    //Todo: true false가 다 존재할땐 기능하지만 개별적으로 존재할때 기능이 동작할지 의문?
    private void setRate(GoalRateDto goalRateDto, Queue<String> socialSt, Queue<Boolean> goalSt) {
        double truecount;
        if (socialSt.isEmpty() && goalSt.isEmpty()) {
            socialSt.offer(goalRateDto.getDayString());
            goalSt.offer(goalRateDto.isCheckGoal());
            totalCount = goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalCount);
                goalRateDto.setRate(Math.round((truecount / totalCount) * 100));
            }
        } else if (socialSt.element().equals(goalRateDto.getDayString()) && goalSt.element() == !goalRateDto.isCheckGoal()) {
            socialSt.poll();
            goalSt.poll();
            totalCount += goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalCount);
                goalRateDto.setRate(Math.round((truecount / totalCount) * 100));
            }
        } else if (!socialSt.element().equals(goalRateDto.getDayString())) {
            socialSt.poll();
            goalSt.poll();
            socialSt.offer(goalRateDto.getDayString());
            goalSt.offer(goalRateDto.isCheckGoal());
            totalCount = goalRateDto.getTotalcount();
            if (goalRateDto.isCheckGoal()) {
                truecount = goalRateDto.getTotalcount();
                goalRateDto.setTotalcount((long) totalCount);
                goalRateDto.setRate(Math.round((truecount / totalCount) * 100));
            }
        }
    }
    // TODO : 달력 날짜 받기X 주간 달성도 리턴하기
    @Transactional
    public ResponseEntity<List<GoalResponseDto>> CreateGoal(GoalDto goalDto, Principaldetail principaldetail) {
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
        if(checkdate(LocalTime.parse(goalDto.time),principaldetail.getMember().getSocialId())){
            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime endDate = localDateTime.plusDays(goalDto.category);
            int period = Period.between(localDateTime.toLocalDate(),endDate.toLocalDate()).getDays();

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
            // 컴백 뱃지 없으면 false 있으면 true
            if(!badgeMap.containsKey("comebackBadge")) {
                if(!member.getGoalList().isEmpty()) {
                    getComebackBadge(member, badgeList);
                }
            }else if(!badgeMap.get("comebackBadge")){
                if(!member.getGoalList().isEmpty()) {
                    getComebackBadge(member, badgeList);
                }
            }
            if(member.getBadgeList().size()>=5){
                checkKingBadge(member, badgeList);
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
                    .msgDto(MsgDto.builder().code(SuccessMsg.CREATE_GOALS.getCode()).msg(SuccessMsg.CREATE_GOALS.getMsg()).build())
                    .build()));
            return ResponseEntity.ok(goalResponseDtoList);
        }
            throw new IllegalArgumentException("하루에 최대 5개까지만 습관 생성이 가능합니다.");
    }

    private void checkKingBadge(Member member, List<Badge> badgeList) {
        if(member.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("뱃지 왕 뱃지"))){
            String message = "축하합니다! 👑 뱃지 왕 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(member)
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("뱃지 왕 뱃지")
                    .member(member)
                    .createdAt(LocalDate.now())
                    .badgeNumber(5)
                    .build());}
    }

    public void getWelcomeBadge(Member member, List<Badge> badgeList) {
        String message = "축하합니다! 🐣 웰컴 뱃지를 획득하셨습니다.";
        notificationService.send(BadgeSseDto.builder()
                .notificationType(NotificationType.Badge)
                .message(message)
                .member(member)
                .build());
        badgeList.add(Badge.realBadgeBuilder()
                .badgeName("웰컴 뱃지")
                .member(member)
                .createdAt(LocalDate.now())
                .badgeNumber(1)
                .build());
    }

    private void getPrivateBadge(Member member, List<Badge> badgeList) {
        List<String> privateBadgeCheckDtoList = member.getGoalList().stream().filter(Goal::isPrivateCheck).map(Goal::getGoalFlag).distinct().collect(Collectors.toList());
        if (privateBadgeCheckDtoList.size() >= 9) {
            String message = "축하합니다! 🔏 프라이빗 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(member)
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("프라이빗 뱃지")
                    .member(member)
                    .createdAt(LocalDate.now())
                    .badgeNumber(2)
                    .build());
        }
    }

    private void getComebackBadge(Member member, List<Badge> badgeList) {
        LocalDate latestDay = member.getGoalList().stream().sorted(Comparator.comparing(Goal::getCurrentDate).reversed()).map(Goal::getCurrentDate).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("목표가 존재하지 않습니다.")).toLocalDate();
        LocalDate today = LocalDate.now();
        Period pe = Period.between(latestDay, today);
        if (pe.getDays() >= 7) {
            String message = "축하합니다! 7️⃣ 컴백 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(member)
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("컴백 뱃지")
                    .member(member)
                    .createdAt(LocalDate.now())
                    .badgeNumber(4)
                    .build());
        }
    }
    private boolean checkdate (LocalTime time,String socialId){
        LocalDateTime localDateTime = LocalDateTime.now();
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

    @Transactional
    public ResponseEntity<GoalResponseDto> ChangeGoal(long goalId,Boolean achievement,Principaldetail principaldetail) {
        if(achievement == null){
            throw new IllegalArgumentException("통신시 달성여부가 보내져야 합니다.");
        }
        Goal goal = goalRepository.findOldGoal(goalId,principaldetail.getMember().getSocialId())
                .map(g -> changeCheckGoal(g,achievement))
                .orElseThrow(() -> new IllegalArgumentException("해당 습관이 존재하지 않습니다."));
        CheckAndGiveBadge(goal);

        if(goal.getTitle().length()<=8) {
            String message = goal.getTitle() + " 습관을 달성하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Complete)
                    .message(message)
                    .member(goal.getMember())
                    .build());
        }
        else {
            String message = goal.getTitle().substring(0, 8) + "... 습관을 달성하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Complete)
                    .message(message)
                    .member(goal.getMember())
                    .build());
        }

        return ResponseEntity.ok().body(GoalResponseDto.builder()
                .currentdate(goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .startDate(goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .endDate(goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")).substring(0,13))
                .title(goal.getTitle())
                .achievementCheck(goal.isAchievementCheck())
                .id(goal.getId())
                .privateCheck(goal.isPrivateCheck())
                .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("습관 달성 축하드립니다.!!! 고생 많으셨어요").build())
                .socialId(goal.getSocialId())
                .characterId(goal.getCharacterId())
                .time(goal.getTime())
                .build());
    }

    private Goal changeCheckGoal(Goal goal,boolean achievement) {
        goal.SetAchievementCheck(achievement);
        return goalRepository.save(goal);
    }


    private void CheckAndGiveBadge(Goal goal) {
        List<Badge> badgeList = new ArrayList<>();
        Map<String, Boolean> badgeMap = new HashMap<>();
        if(!goal.getMember().getBadgeList().isEmpty()){
            badgeMap.put("plopBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("퐁당 퐁당 뱃지")));
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
        LocalTime dawnStart = LocalTime.of(22,59,59);
        LocalTime dawnEnd = LocalTime.of(2,0,1);
        LocalTime shortTime = LocalTime.of(0,3,1);
        LocalTime longTime = LocalTime.of(1,59,59);
        if(!badgeMap.containsKey("plopBadge")){
            checkPlopBadge(goal, badgeList);
        }else if(!badgeMap.get("plopBadge")){
            checkPlopBadge(goal, badgeList);
        }
        if(!badgeMap.containsKey("earlyBirdBadge")){ //얼리버드 뱃지
            earlyMorningBadge(goal, badgeList, earlyStart, earlyEnd);
        }else if (!badgeMap.get("earlyBirdBadge")){
            earlyMorningBadge(goal, badgeList, earlyStart, earlyEnd);
        }
        if(!badgeMap.containsKey("owlBirdBadge")){ //올빼미 뱃지
            owlBadge(goal, badgeList, dawnStart, dawnEnd);
        }else if (!badgeMap.get("owlBirdBadge")){
            owlBadge(goal, badgeList, dawnStart, dawnEnd);
        }
        if(!badgeMap.containsKey("shortTimeBadge")){  //단타 뱃지
            shortBadge(goal, badgeList, shortTime);
        }else if (!badgeMap.get("shortTimeBadge")){
            shortBadge(goal, badgeList, shortTime);
        }
        if(!badgeMap.containsKey("longTimeBadge")){   //장타 뱃지
            longBadge(goal, badgeList, longTime);
        }else if (!badgeMap.get("longTimeBadge")){
            longBadge(goal, badgeList, longTime);
        }
        if(!badgeMap.containsKey("minBadge")){    //미니멈 뱃지
            checkMinBadge(goal, badgeList);
        }else if(!badgeMap.get("minBadge")){
            checkMinBadge(goal, badgeList);
        }
        if(!badgeMap.containsKey("maxBadge")){    //맥시멈 뱃지
            checkMaxBadge(goal,badgeList);
        }else if (!badgeMap.get("maxBadge")){
            checkMaxBadge(goal,badgeList);
        }
        if(goal.getMember().getBadgeList().size()>=5){
            checkKingBadge(goal.getMember(),badgeList);
        }
        if(!badgeMap.containsKey("shortWayBadge")){ //단거리 뱃지
            int shortWay = 10;
            int continueCnt = getContinueCnt(goal, shortWay);
            judgeBadge(goal, badgeList, shortWay, continueCnt);
        }else if (!badgeMap.get("shortWayBadge")){
            int shortWay = 10;
            int continueCnt = getContinueCnt(goal, shortWay);
            judgeBadge(goal, badgeList, shortWay, continueCnt);
        }
        if(!badgeMap.containsKey("middleWayBadge")){
            int middleWay = 20;
            int continueCnt = getContinueCnt(goal, middleWay);
            judgeBadge(goal, badgeList, middleWay, continueCnt);
        }else if(!badgeMap.get("middleWayBadge")){
            int middleWay = 20;
            int continueCnt = getContinueCnt(goal, middleWay);
            judgeBadge(goal, badgeList, middleWay, continueCnt);
        } if(!badgeMap.containsKey("longWayBadge")){
            int longWay = 30;
            int continueCnt = getContinueCnt(goal, longWay);
            judgeBadge(goal, badgeList, longWay, continueCnt);
        }else if(!badgeMap.get("longWayBadge")){
            int longWay = 30;
            int continueCnt = getContinueCnt(goal, longWay);
            judgeBadge(goal, badgeList, longWay, continueCnt);
        }
        if(!badgeList.isEmpty())
            badgeRepository.saveAll(badgeList);
    }

    private void checkPlopBadge(Goal goal, List<Badge> badgeList) {
        Badge badge = badgeUtil.plopBadge(goal);
        if(!badge.getBadgeName().equals("가짜 뱃지")){
            String message = "축하합니다! 🐬 퐁당 퐁당 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(badge);
        }
    }

    private void earlyMorningBadge(Goal goal, List<Badge> badgeList, LocalTime earlyStart, LocalTime earlyEnd) {
        if(LocalTime.now().isAfter(earlyStart) && LocalTime.now().isBefore(earlyEnd)){
            String message = "축하합니다! 🐤 얼리버드 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeNumber(7)
                    .badgeName("얼리버드 뱃지")
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void owlBadge(Goal goal, List<Badge> badgeList, LocalTime dawnStart, LocalTime dawnEnd) {
        if(LocalTime.now().isAfter(dawnStart) || LocalTime.now().isBefore(dawnEnd)){
            String message = "축하합니다! 🦉 올빼미 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("올빼미 뱃지")
                    .badgeNumber(8)
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void shortBadge(Goal goal, List<Badge> badgeList, LocalTime shortTime) {
        if(LocalTime.parse(goal.getTime()).isBefore(shortTime)){
            String message = "축하합니다! 🍜 단타 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .badgeName("단타 뱃지")
                    .badgeNumber(16)
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void longBadge(Goal goal, List<Badge> badgeList, LocalTime longTime) {
        if(LocalTime.parse(goal.getTime()).isAfter(longTime)){
            String message = "축하합니다! 🕑 장타 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeNumber(17)
                    .badgeName("장타 뱃지")
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void checkMinBadge(Goal goal, List<Badge> badgeList) {
        List<Goal> goalList = goal.getMember().getGoalList()
                .stream()
                .sorted(Comparator.comparing(Goal::getCurrentDate).reversed())
                .limit(28)
                .collect(Collectors.toList());
        if(goalList.stream().allMatch(Goal::isAchievementCheck) && goalList.size() >= 7){
            boolean sufficeBadge = isSufficeBadge(goalList);
            if(sufficeBadge){
                String message = "축하합니다! ⭐ 미니멈 뱃지를 획득하셨습니다.";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Badge)
                        .message(message)
                        .member(goal.getMember())
                        .build());
                badgeList.add(Badge.realBadgeBuilder()
                        .member(goal.getMember())
                        .badgeName("미니멈 뱃지")
                        .badgeNumber(14)
                        .createdAt(LocalDate.now())
                        .build());
            }
        }
    }

    private void checkMaxBadge(Goal goal, List<Badge> badgeList) {
        List<Goal> goalList = goal.getMember().getGoalList()
                .stream()
                .sorted(Comparator.comparing(Goal::getCurrentDate).reversed())
                .limit(35)
                .collect(Collectors.toList());
        int size = goalList.size();
        boolean sufficeBadge = false;
        if(goalList.stream().allMatch(Goal::isAchievementCheck) && size == 35){
            for(int i= 0; i<size; i++ ){
                if(i%5<4){
                    if(!goalList.get(i).getCurrentDate().toLocalDate().equals(goalList.get(i+1).getCurrentDate().toLocalDate())){
                        sufficeBadge = false;
                        break;
                    }
                }else if (i != size-1){
                    if(goalList.get(i).getCurrentDate().toLocalDate().equals(goalList.get(i+1).getCurrentDate().toLocalDate())) {
                        sufficeBadge = false;
                        break;
                    }
                }
                sufficeBadge = true;
            }if(sufficeBadge){
                String message = "축하합니다! 🌟 맥시멈 뱃지를 획득하셨습니다";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Badge)
                        .message(message)
                        .member(goal.getMember())
                        .build());
                badgeList.add(Badge.realBadgeBuilder()
                        .member(goal.getMember())
                        .badgeName("맥시멈 뱃지")
                        .badgeNumber(15)
                        .createdAt(LocalDate.now())
                        .build());
            }
        }
    }

    private boolean isSufficeBadge(List<Goal> goalList) {
        int limit = goalList.size();
        Queue<LocalDateTime> checkDate = new LinkedList<>();
        int minCheckCnt=1;
        int sameDateCnt = 1;
        boolean sufficeBadge = false;
        for(int i=0; i<limit; i++){
            if(checkDate.isEmpty()){
                checkDate.offer(goalList.get(i).getCurrentDate());
            }
            else {
                if(!checkDate.peek().equals(goalList.get(i).getCurrentDate())) {
                    checkDate.poll();
                    checkDate.offer(goalList.get(i).getCurrentDate());
                    minCheckCnt++;
                    if (minCheckCnt > 7) {
                        break;
                    }
                    if(sameDateCnt == 5){
                        break;
                    } else sameDateCnt = 1;
                }else sameDateCnt++;
            }
        }
        if(minCheckCnt == 7) {
            sufficeBadge = true;
        }
        return sufficeBadge;
    }


    private int getContinueCnt(Goal goal, int standardWay) {
        List<LocalDate> goalDateList = goal.getMember().getGoalList()
                .stream()
                .filter(Goal::isAchievementCheck)
                .sorted(Comparator.comparing(Goal::getCurrentDate).reversed())
                .map(g->g.getCurrentDate().toLocalDate()).distinct()
                .collect(Collectors.toList());
        int size = goalDateList.size();
        int continueCnt = 1;
        if(size>=10){
            for(int i=0; i<size-1; i++){
                if(Period.between(goalDateList.get(i),goalDateList.get(i+1)).getDays()>1){
                    continueCnt = 0;
                    break;
                }
                else{
                    if(continueCnt > standardWay -1)
                        break;
                    else continueCnt++;
                }
            }
        }
        return continueCnt;
    }

    private void judgeBadge(Goal goal, List<Badge> badgeList, int standardWay, int continueCnt) {
        if(continueCnt == standardWay && continueCnt == 10) {
            String message = "축하합니다! 🥉 단거리 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .badgeName("단거리 뱃지")
                    .badgeNumber(11)
                    .build());
        }
        else if(continueCnt == standardWay && continueCnt == 20) {
            String message = "축하합니다! 🥈 중거리 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .badgeName("중거리 뱃지")
                    .badgeNumber(12)
                    .build());
        }
        else if(continueCnt == standardWay && continueCnt == 30){
            String message = "축하합니다! 🥇 장거리 뱃지를 획득하셨습니다.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .badgeName("장거리 뱃지")
                    .badgeNumber(13)
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
            goalList.forEach(this::cutMapping);
            goalRepository.deleteAll(goalList);
        }
        return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("만드셨던"+ category +"일치의 습관을 모두 삭제하셨습니다.").build());
    }

    private void cutMapping(Goal goal) {
        goal.getMember().getGoalList().remove(goal);
    }
}