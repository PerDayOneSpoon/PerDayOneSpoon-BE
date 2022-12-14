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
import org.springframework.context.ApplicationEventPublisher;
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

    private final ApplicationEventPublisher eventPublisher;
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
    Map<Integer , Double> map = new HashMap<>();
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
                    if(y < 7){
                        weekRateDtoList.add(y-1,WeekRateDto.builder().id(y).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                    }
                    else weekRateDtoList.add(0,WeekRateDto.builder().id(0).rate(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
            }
            else {
                if(y < 7){
                    weekRateDtoList.add(y-1,WeekRateDto.builder().rate(0).id(y).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());
                }
                else weekRateDtoList.add(0,WeekRateDto.builder().rate(0).id(0).dayString(DayOfWeek.of(y).getDisplayName(TextStyle.SHORT, Locale.KOREAN)).build());}
        }
        if(!dayList.isEmpty()){
            dayList.clear();
        }
        List<WeekRateDto> weekRateDtos = weekRateDtoList.stream().sorted(Comparator.comparing(WeekRateDto::getId)).collect(Collectors.toList());
        List<TodayGoalsDto> todayGoalsDtoList = goalRepository.getTodayGoal(LocalDateTime.now(),principaldetail.getMember().getSocialId());
        AchivementResponseDto achivementResponseDto = AchivementResponseDto.builder()
                .weekRateDtoList(weekRateDtos)
                .todayGoalsDtoList(todayGoalsDtoList)
                .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("?????? ?????? ????????? ?????????????????????. ????????????!").build())
                .weekStartDate(sunday.format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                .weekEndDate(saturday.format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                .currentDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
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
    //Todo: true false??? ??? ???????????? ??????????????? ??????????????? ???????????? ????????? ???????????? ???????
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
    @Transactional
    // TODO : ?????? ?????? ??????X ?????? ????????? ????????????
    public ResponseEntity<List<GoalResponseDto>> CreateGoal(GoalDto goalDto, Principaldetail principaldetail) {
        String goalFlag = UUID.randomUUID().toString();
        if(goalDto.getTitle() == null) {
            throw new IllegalArgumentException("????????? ??????????????????");
        } else if (goalDto.getCharacterId() == 0){
            throw new IllegalArgumentException("???????????? ????????? ?????????");
        }
        Member member = memberRepository.findByMemberId(principaldetail.getMember().getId()).orElseThrow(
                () -> new IllegalArgumentException("?????? ????????? ????????????."));

        Map<String , Boolean> badgeMap = new HashMap<>();

        if(!member.getBadgeList().isEmpty()) {
            badgeMap.put("welcomeBadge",member.getBadgeList().stream().anyMatch(badge -> badge.getBadgeName().equals("?????? ??????")));
            badgeMap.put("privateBadge",member.getBadgeList().stream().anyMatch(badge -> badge.getBadgeName().equals("???????????? ??????")));
            badgeMap.put("comebackBadge",member.getBadgeList().stream().anyMatch(badge -> badge.getBadgeName().equals("?????? ??????")));
        }

        int x=0;
        List<Goal> goalList = new ArrayList<>();
        if(checkdate(LocalTime.parse(goalDto.time),principaldetail.getMember().getSocialId())){
            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime endDate = localDateTime.plusDays(goalDto.category);
            int period = Period.between(localDateTime.toLocalDate(),endDate.toLocalDate()).getDays();

            // ???????????? ????????? false ????????? true
            List<Badge> badgeList = new ArrayList<>();
            if(!badgeMap.containsKey("welcomeBadge")) {
                getWelcomeBadge(member, badgeList);
            }else if(!badgeMap.get("welcomeBadge")) {
                getWelcomeBadge(member, badgeList);
            }
            // ???????????? ?????? ????????? false ????????? true
            if(!badgeMap.containsKey("privateBadge")) {
                getPrivateBadge(member, badgeList);
            } else if (!badgeMap.get("privateBadge")){
                getPrivateBadge(member, badgeList);
            }
            // ?????? ?????? ????????? false ????????? true
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
            if(!badgeList.isEmpty()) {
                badgeRepository.saveAll(badgeList);
            }
            List<GoalResponseDto> goalResponseDtoList = new ArrayList<>();
            goalList.forEach(Goal -> goalResponseDtoList.add(GoalResponseDto.builder()
                    .currentdate(Goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                    .achievementCheck(Goal.isAchievementCheck())
                    .id(Goal.getId())
                    .socialId(Goal.getSocialId())
                    .title(Goal.getTitle())
                    .characterUrl(GetCharacterUrl.getMandooUrl(Goal.getCharacterId()))
                    .endDate(Goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                    .startDate(Goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                    .privateCheck(Goal.isPrivateCheck())
                    .time(Goal.getTime())
                    .goalFlag(Goal.getGoalFlag())
                    .msgDto(MsgDto.builder().code(SuccessMsg.CREATE_GOALS.getCode()).msg(SuccessMsg.CREATE_GOALS.getMsg()).build())
                    .build()));
            return ResponseEntity.ok(goalResponseDtoList);
        }
            throw new IllegalArgumentException("????????? ?????? 5???????????? ?????? ????????? ???????????????.");
    }

    private void checkKingBadge(Member member, List<Badge> badgeList) {
        if(member.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("?????? ??? ??????"))){
            String message = "???????????????! ???? ?????? ??? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(member)
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("?????? ??? ??????")
                    .member(member)
                    .createdAt(LocalDate.now())
                    .badgeNumber(5)
                    .build());}
    }

    private void getWelcomeBadge(Member member, List<Badge> badgeList) {
        String message = "???????????????! ???? ?????? ????????? ?????????????????????.";
        eventPublisher.publishEvent(BadgeSseDto.builder()
                .notificationType(NotificationType.Badge)
                .message(message)
                .member(member)
                .build());
//        notificationService.send(BadgeSseDto.builder()
//                .notificationType(NotificationType.Badge)
//                .message(message)
//                .member(member)
//                .build());
        badgeList.add(Badge.realBadgeBuilder()
                .badgeName("?????? ??????")
                .member(member)
                .createdAt(LocalDate.now())
                .badgeNumber(1)
                .build());
    }

    private void getPrivateBadge(Member member, List<Badge> badgeList) {
        List<String> privateBadgeCheckDtoList = member.getGoalList().stream().filter(Goal::isPrivateCheck).map(Goal::getGoalFlag).distinct().collect(Collectors.toList());
        if (privateBadgeCheckDtoList.size() >= 9) {
            String message = "???????????????! ???? ???????????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(member)
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("???????????? ??????")
                    .member(member)
                    .createdAt(LocalDate.now())
                    .badgeNumber(2)
                    .build());
        }
    }

    private void getComebackBadge(Member member, List<Badge> badgeList) {
        LocalDate latestDay = member.getGoalList().stream()
                .sorted(Comparator.comparing(Goal::getCurrentDate).reversed())
                .map(Goal::getCurrentDate)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("????????? ???????????? ????????????.")).toLocalDate();
        LocalDate today = LocalDate.now();
        Period pe = Period.between(latestDay, today);
        if (pe.getDays() >= 7) {
            String message = "???????????????! 7?????? ?????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(member)
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("?????? ??????")
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
                throw new IllegalArgumentException("????????? ????????? ?????? 5???????????? ???????????????. ?????? ??????????????????");
            }
        }
        if(time.getHour() == 0 && time.getMinute() == 0){
            throw new IllegalArgumentException("????????? ????????? ???????????? ????????? ????????? ??????????????????");
        }
        if(localDateTime.getDayOfMonth() == localDateTime.plusHours(time.getHour()).getDayOfMonth()) {
            LocalDateTime localDateTime1 = localDateTime.plusHours(time.getHour());
            if (localDateTime.getDayOfMonth() == localDateTime1.plusMinutes(time.getMinute()).getDayOfMonth()) {
                return true;
            } else
                throw new IllegalArgumentException("????????? ?????? ????????? ????????? ??? ????????????. ?????? ????????? ?????????");
        }
        else
            throw new IllegalArgumentException("????????? ?????? ????????? ????????? ??? ????????????. ?????? ????????? ?????????");
    }

    public ResponseEntity<GoalResponseDto> ChangeGoal(long goalId,Boolean achievement,Principaldetail principaldetail) {
        if(achievement == null){
            throw new IllegalArgumentException("????????? ??????????????? ???????????? ?????????.");
        }
        Goal goal = goalRepository.findOldGoal(goalId,principaldetail.getMember().getSocialId())
                .map(g -> changeCheckGoal(g,achievement))
                .orElseThrow(() -> new IllegalArgumentException("?????? ????????? ???????????? ????????????."));
        CheckAndGiveBadge(goal);

        if(goal.getTitle().length()<=8) {
            String message = goal.getTitle() + " ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Complete)
                    .message(message)
                    .member(goal.getMember())
                    .build());
        }
        else {
            String message = goal.getTitle().substring(0, 8) + "... ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Complete)
                    .message(message)
                    .member(goal.getMember())
                    .build());
        }

        return ResponseEntity.ok().body(GoalResponseDto.builder()
                .currentdate(goal.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                .startDate(goal.getStartDate().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                .endDate(goal.getEndDate().format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???")).substring(0,13))
                .title(goal.getTitle())
                .achievementCheck(goal.isAchievementCheck())
                .id(goal.getId())
                .privateCheck(goal.isPrivateCheck())
                .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("?????? ?????? ??????????????????.!!! ?????? ???????????????").build())
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
            badgeMap.put("plopBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("?????? ?????? ??????")));
            badgeMap.put("earlyBirdBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("???????????? ??????")));
            badgeMap.put("owlBirdBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("????????? ??????")));
            badgeMap.put("shortTimeBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("?????? ??????")));
            badgeMap.put("longTimeBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("?????? ??????")));
            badgeMap.put("minBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("????????? ??????")));
            badgeMap.put("maxBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("????????? ??????")));
            badgeMap.put("shortWayBadge", goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("????????? ??????")));
            badgeMap.put("middleWayBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("????????? ??????")));
            badgeMap.put("longWayBadge",goal.getMember().getBadgeList().stream().anyMatch(f->f.getBadgeName().equals("????????? ??????")));
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
        if(!badgeMap.containsKey("earlyBirdBadge")){ //???????????? ??????
            earlyMorningBadge(goal, badgeList, earlyStart, earlyEnd);
        }else if (!badgeMap.get("earlyBirdBadge")){
            earlyMorningBadge(goal, badgeList, earlyStart, earlyEnd);
        }
        if(!badgeMap.containsKey("owlBirdBadge")){ //????????? ??????
            owlBadge(goal, badgeList, dawnStart, dawnEnd);
        }else if (!badgeMap.get("owlBirdBadge")){
            owlBadge(goal, badgeList, dawnStart, dawnEnd);
        }
        if(!badgeMap.containsKey("shortTimeBadge")){  //?????? ??????
            shortBadge(goal, badgeList, shortTime);
        }else if (!badgeMap.get("shortTimeBadge")){
            shortBadge(goal, badgeList, shortTime);
        }
        if(!badgeMap.containsKey("longTimeBadge")){   //?????? ??????
            longBadge(goal, badgeList, longTime);
        }else if (!badgeMap.get("longTimeBadge")){
            longBadge(goal, badgeList, longTime);
        }
        if(!badgeMap.containsKey("minBadge")){    //????????? ??????
            checkMinBadge(goal, badgeList);
        }else if(!badgeMap.get("minBadge")){
            checkMinBadge(goal, badgeList);
        }
        if(!badgeMap.containsKey("maxBadge")){    //????????? ??????
            checkMaxBadge(goal,badgeList);
        }else if (!badgeMap.get("maxBadge")){
            checkMaxBadge(goal,badgeList);
        }
        if(goal.getMember().getBadgeList().size()>=5){
            checkKingBadge(goal.getMember(),badgeList);
        }
        if(!badgeMap.containsKey("shortWayBadge")){ //????????? ??????
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
        if(!badge.getBadgeName().equals("?????? ??????")){
            String message = "???????????????! ???? ?????? ?????? ????????? ?????????????????????.";
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
            String message = "???????????????! ???? ???????????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeNumber(7)
                    .badgeName("???????????? ??????")
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void owlBadge(Goal goal, List<Badge> badgeList, LocalTime dawnStart, LocalTime dawnEnd) {
        if(LocalTime.now().isAfter(dawnStart) || LocalTime.now().isBefore(dawnEnd)){
            String message = "???????????????! ???? ????????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("????????? ??????")
                    .badgeNumber(8)
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void shortBadge(Goal goal, List<Badge> badgeList, LocalTime shortTime) {
        if(LocalTime.parse(goal.getTime()).isBefore(shortTime)){
            String message = "???????????????! ???? ?????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .badgeName("?????? ??????")
                    .badgeNumber(16)
                    .createdAt(LocalDate.now())
                    .build());
        }
    }

    private void longBadge(Goal goal, List<Badge> badgeList, LocalTime longTime) {
        if(LocalTime.parse(goal.getTime()).isAfter(longTime)){
            String message = "???????????????! ???? ?????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeNumber(17)
                    .badgeName("?????? ??????")
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
                String message = "???????????????! ??? ????????? ????????? ?????????????????????.";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Badge)
                        .message(message)
                        .member(goal.getMember())
                        .build());
                badgeList.add(Badge.realBadgeBuilder()
                        .member(goal.getMember())
                        .badgeName("????????? ??????")
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
                String message = "???????????????! ???? ????????? ????????? ?????????????????????";
                notificationService.send(BadgeSseDto.builder()
                        .notificationType(NotificationType.Badge)
                        .message(message)
                        .member(goal.getMember())
                        .build());
                badgeList.add(Badge.realBadgeBuilder()
                        .member(goal.getMember())
                        .badgeName("????????? ??????")
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
            String message = "???????????????! ???? ????????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .badgeName("????????? ??????")
                    .badgeNumber(11)
                    .build());
        }
        else if(continueCnt == standardWay && continueCnt == 20) {
            String message = "???????????????! ???? ????????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .badgeName("????????? ??????")
                    .badgeNumber(12)
                    .build());
        }
        else if(continueCnt == standardWay && continueCnt == 30){
            String message = "???????????????! ???? ????????? ????????? ?????????????????????.";
            notificationService.send(BadgeSseDto.builder()
                    .notificationType(NotificationType.Badge)
                    .message(message)
                    .member(goal.getMember())
                    .build());
            badgeList.add(Badge.realBadgeBuilder()
                    .member(goal.getMember())
                    .createdAt(LocalDate.now())
                    .badgeName("????????? ??????")
                    .badgeNumber(13)
                    .build());
        }
    }

    public ResponseEntity deleteGoals(Principaldetail principaldetail, String deleteFlag) {
        List<Goal> goalList = goalRepository.getCategoryGoals(principaldetail.getMember().getSocialId(),deleteFlag);
        int category;
        if(goalList.isEmpty()){
            throw new IllegalArgumentException("?????? ????????? ???????????? ????????????.");
        }
        else {
            category = goalList.get(0).getCategory();
            goalList.forEach(this::cutMapping);
            goalRepository.deleteAll(goalList);
        }
        return ResponseEntity.ok().body(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("????????????"+ category +"????????? ????????? ?????? ?????????????????????.").build());
    }

    private void cutMapping(Goal goal) {
        goal.getCommentList().clear();
        goal.getMember().getGoalList().remove(goal);

    }
}