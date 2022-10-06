package com.sparta.perdayonespoon.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.perdayonespoon.comment.domain.entity.Comment;
import com.sparta.perdayonespoon.comment.domain.entity.QComment;
import com.sparta.perdayonespoon.domain.Goal;
import com.sparta.perdayonespoon.domain.QImage;
import com.sparta.perdayonespoon.domain.dto.CountDto;
import com.sparta.perdayonespoon.domain.dto.QCountDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.EveryTwoDaysGoalDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.QEveryTwoDaysGoalDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.QTodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.Goal.TodayGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.PrivateBadgeCheckDto;
import com.sparta.perdayonespoon.domain.dto.response.QPrivateBadgeCheckDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.CalendarGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.calendar.QCalendarGoalsDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.GoalRateDto;
import com.sparta.perdayonespoon.domain.dto.response.rate.QGoalRateDto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sparta.perdayonespoon.comment.domain.entity.QComment.comment;
import static com.sparta.perdayonespoon.domain.QBadge.badge;
import static com.sparta.perdayonespoon.domain.QGoal.goal;
import static com.sparta.perdayonespoon.domain.QHeart.heart;
import static com.sparta.perdayonespoon.domain.QImage.image;
import static com.sparta.perdayonespoon.domain.QMember.member;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<GoalRateDto> getRateGoal(LocalDateTime sunday, LocalDateTime saturday, String socialId){
        return queryFactory
                .select(new QGoalRateDto(
                        goal.currentDate.stringValue().substring(0,10),
                        goal.achievementCheck,goal.count()))
                .from(goal)
                .where(goal.currentDate.dayOfYear().between(sunday.getDayOfYear(),saturday.getDayOfYear()),
                        GoalSocialEq(socialId))
                .groupBy(goal.currentDate.stringValue().substring(0,10),goal.achievementCheck)
                .fetch();
    }

    @Override
    public Optional<CountDto> getCountGoal(LocalDateTime currentDate,String socialId){
        return Optional.ofNullable(queryFactory
                .select(new QCountDto(
                        goal.currentDate.stringValue().substring(0,10),
                        goal.count()))
                .from(goal)
                .where(GoalCurrentEq(currentDate.getDayOfYear()),
                        GoalSocialEq(socialId))
                .groupBy(goal.currentDate.stringValue().substring(0,10))
                .fetchOne());
    }

    @Override
    public List<TodayGoalsDto> getTodayGoal(LocalDateTime currentDate,String socialId){
        return queryFactory
                .select(new QTodayGoalsDto(
                        goal.title,
                        goal.startDate,
                        goal.endDate,
                        goal.time,
                        goal.characterId,
                        goal.id,
                        goal.privateCheck,
                        goal.currentDate,
                        goal.achievementCheck,
                        goal.heartList.size(),
                        goal.goalFlag,
                        JPAExpressions.selectFrom(heart).where(heart.goal.id.eq(goal.id),heart.socialId.eq(socialId)).exists()
                ))
                .from(goal)
                .where(goal.currentDate.dayOfYear().eq(currentDate.getDayOfYear()),
                        GoalSocialEq(socialId))
                .fetch();
    }
    @Override
    public List<Goal> getMyTodayGoal(LocalDateTime currentDate, String socialId){
        return queryFactory
                .selectFrom(goal)
                .where(goal.currentDate.dayOfYear().eq(currentDate.getDayOfYear()),
                        GoalSocialEq(socialId))
                .join(goal.member,member).fetchJoin()
                .leftJoin(goal.heartList,heart).fetchJoin()
                .leftJoin(goal.commentList,comment)
                .distinct()
                .fetch();
    }

    @Override
    public List<Goal> getFollwerTodayGoal(LocalDateTime currentDate, Long friendId, boolean privateCheck){
        return queryFactory
                .selectDistinct(goal)
                .from(goal)
                .where(goal.currentDate.dayOfYear().eq(currentDate.getDayOfYear()),
                        GoalPrivateEq(privateCheck),goal.member.id.eq(friendId))
                .join(goal.member,member).fetchJoin()
                .leftJoin(goal.heartList,heart).fetchJoin()
                .leftJoin(goal.commentList,comment)
                .fetch();
    }


    @Override
    public List<CalendarGoalsDto> getMyCalendar(LocalDate startDate, LocalDate endDate, String socialId){
        return queryFactory
                .select(new QCalendarGoalsDto(
                        goal.id,
                        goal.title,
                        goal.startDate,
                        goal.endDate,
                        goal.currentDate,
                        goal.time,
                        goal.characterId,
                        goal.privateCheck,
                        goal.achievementCheck))
                .from(goal)
                .where(goal.currentDate.dayOfYear().between(startDate.getDayOfYear(),endDate.getDayOfYear()),
                        GoalSocialEq(socialId))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    @Override
    public List<CalendarGoalsDto> getSpecificCalender(LocalDate startDate, LocalDate endDate,String socialId){
        return queryFactory
                .select(new QCalendarGoalsDto(
                        goal.id,
                        goal.title,
                        goal.startDate,
                        goal.endDate,
                        goal.currentDate,
                        goal.time,
                        goal.characterId,
                        goal.privateCheck,
                        goal.achievementCheck))
                .from(goal)
                .where(goal.currentDate.dayOfYear().between(startDate.getDayOfYear(),endDate.getDayOfYear()),
                        GoalSocialEq(socialId))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }


    @Override
    public List<CalendarGoalsDto> getFriendCalendar(LocalDate startDate, LocalDate endDate, boolean privateCheck,
                                                    Long goalId){
        return queryFactory.select(
                new QCalendarGoalsDto(
                        goal.id,
                        goal.title,
                        goal.startDate,
                        goal.endDate,
                        goal.currentDate,
                        goal.time,
                        goal.characterId,
                        goal.privateCheck,
                        goal.achievementCheck))
                .from(goal)
                .rightJoin(member).on(goal.socialId.eq(member.socialId),member.id.eq(goalId))
                .where(goal.currentDate.dayOfYear().between(startDate.getDayOfYear(),endDate.getDayOfYear()),
                        GoalPrivateEq(privateCheck))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    @Override
    public List<CalendarGoalsDto> getFriendSpecificCalendar(LocalDate startDate, LocalDate endDate,boolean privateCheck, Long goalId){
        return queryFactory.select(
                new QCalendarGoalsDto(
                        goal.id,
                        goal.title,
                        goal.startDate,
                        goal.endDate,
                        goal.currentDate,
                        goal.time,
                        goal.characterId,
                        goal.privateCheck,
                        goal.achievementCheck))
                .from(goal)
                .rightJoin(member).on(goal.socialId.eq(member.socialId),member.id.eq(goalId))
                .where(goal.currentDate.dayOfYear().between(startDate.getDayOfYear(),endDate.getDayOfYear()),
                        GoalPrivateEq(privateCheck))
                .orderBy(goal.currentDate.asc())
                .fetch();
    }

    @Override
    public List<Goal> getCategoryGoals(String socialId, String deleteFlag){
        return queryFactory
                .selectFrom(goal)
                .where(GoalSocialEq(socialId),GoalFlagEq(deleteFlag))
                .leftJoin(goal.commentList,comment).fetchJoin()
                .innerJoin(goal.member,member).fetchJoin()
                .innerJoin(member.image, image).fetchJoin()
                .distinct()
                .fetch();
    }

    @Override
    public Optional<Goal> findOldGoal(Long id, String socialId){
        return Optional.ofNullable(queryFactory
                .select(goal)
                .from(goal)
                .where(goal.id.eq(id),goal.socialId.eq(socialId))
                .innerJoin(goal.member,member).fetchJoin()
                .leftJoin(member.badgeList,badge)
                .fetchOne());
    }

    private BooleanExpression GoalIdEq(Long goalId) {
        return isEmpty(goalId) ? null : goal.id.eq(goalId);
    }

    private BooleanExpression GoalSocialEq(String socialId) {
        return isEmpty(socialId) ? null : goal.socialId.eq(socialId);
    }

    private BooleanExpression GoalCurrentEq(int currentDate) {
        return isEmpty(currentDate) ? null : goal.currentDate.dayOfYear().eq(currentDate);
    }

    private BooleanExpression GoalPrivateEq(boolean privateCheck) {
        return isEmpty(privateCheck) ? null : goal.privateCheck.eq(privateCheck);
    }

    private BooleanExpression GoalFlagEq(String goalFlag){
        return isEmpty(goalFlag) ? null : goal.goalFlag.eq(goalFlag);
    }
}
