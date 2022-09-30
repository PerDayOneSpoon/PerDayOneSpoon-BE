package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.Friend;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.FriendResponseDto;
import com.sparta.perdayonespoon.domain.follow.FollowResponseDto;
import com.sparta.perdayonespoon.domain.follow.FriendDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.repository.FriendRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.util.MsgUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendService {

    private final MsgUtil msgUtil;
    private final BadgeRepository badgeRepository;
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    public ResponseEntity<FriendResponseDto> addFriend(Principaldetail principaldetail, String friendId) {
        if(principaldetail.getMember().getSocialId().equals(friendId)){
            throw new IllegalArgumentException("자신은 팔로우 할 수 없습니다.");
        }
        if(isFollowBetween(friendId,principaldetail.getMember().getSocialId())){
            throw new IllegalArgumentException("이미 팔로우를 신청하셨습니다.");
        }
        List<Member> memberList = memberRepository.getTwoMember(principaldetail.getMember().getSocialId(),friendId);
        if(memberList.size()<2){
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        }
        Member badgeOwner;
        Member friendMember;
        if(memberList.get(0).getSocialId().equals(friendId)){
            friendMember = memberList.get(0);
            badgeOwner = memberList.get(1);
        }else{
            badgeOwner = memberList.get(0);
            friendMember = memberList.get(1);
        }

        //내가 팔로우 누른순간 추가하는건데
        // 나 <--> 친구 의 관계가 어떻게 정립되는지

        Friend friend = Friend.builder().followerId(friendMember.getSocialId()).followingId(principaldetail.getMember().getSocialId()).build();
        friendRepository.save(friend);
        // 인싸 뱃지를 구하기 위한 로직
        List<Badge> badgeList = new ArrayList<>();
        if(badgeOwner.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("인싸 뱃지"))){
            List<Friend> friendList = friendRepository.getBothFollow(principaldetail.getMember().getSocialId());
            popularBadge(principaldetail, badgeOwner, badgeList, friendList);
        }
        if(friendMember.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("인싸 뱃지"))){
            List<Friend> friendList = friendRepository.getBothFollow(principaldetail.getMember().getSocialId());
            popularBadge(principaldetail,friendMember,badgeList,friendList);
        }
        if(badgeOwner.getBadgeList().size()>4){
            kingBadge(badgeOwner, badgeList);
        }
        if(friendMember.getBadgeList().size()>4){
            kingBadge(friendMember, badgeList);
        }
        if(!badgeList.isEmpty()) badgeRepository.saveAll(badgeList);
        return ResponseEntity.ok().body(FriendResponseDto.builder()
                .followCheck(true)
                .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK,"팔로우를 신청하셨습니다."))
                .build());
    }

    private void popularBadge(Principaldetail principaldetail, Member badgeOwner, List<Badge> badgeList, List<Friend> friendList) {
        List<String> followerFriendList = friendList.stream()
                .filter(f->f.getFollowerId().equals(principaldetail.getMember().getSocialId()))
                .map(Friend::getFollowingId)
                .collect(Collectors.toList());
        int standardNumber = followerFriendList.size();
        int friendNumber =0;
        if(standardNumber>4) {
            Set<String> followingFriendList = friendList.stream()
                    .filter(f->f.getFollowingId().equals(principaldetail.getMember().getSocialId()))
                    .map(Friend::getFollowerId)
                    .collect(Collectors.toSet());
            for(int i=0; i<standardNumber; i++){
                if(followingFriendList.contains(followerFriendList.get(i))){
                    friendNumber++;
                    if(friendNumber>4)
                        break;
                }
            }
            if(friendNumber >= 5){
                badgeList.add(Badge.realBadgeBuilder()
                        .badgeName("인싸 뱃지")
                        .member(badgeOwner)
                        .createdAt(LocalDateTime.now().toLocalDate())
                        .badgeNumber(6)
                        .build());
            }
        }
    }

    private void kingBadge(Member badgeOwner, List<Badge> badgeList) {
        if(badgeOwner.getBadgeList().stream().noneMatch(b->b.getBadgeName().equals("뱃지 왕 뱃지"))){
            badgeList.add(Badge.realBadgeBuilder()
                    .badgeName("뱃지 왕 뱃지")
                    .member(badgeOwner)
                    .createdAt(LocalDate.now())
                    .badgeNumber(5)
                    .build());
        }
    }

    public ResponseEntity<FriendResponseDto> deleteFollowerFriend(Principaldetail principaldetail, String friendId) {
        friendRepository.findByFollowerIdAndFollowingId(friendId,principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 팔로우를 취소하셨습니다."));
        return ResponseEntity.ok().body(FriendResponseDto.builder()
                .followCheck(false)
                .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK,"팔로우를 끊으셨습니다."))
                .build());
    }

    public boolean isFollowBetween(String user, String checkUser) {
        return friendRepository.findByFollowerIdAndFollowingId(user, checkUser).isPresent();
    }

    private boolean delete(Friend friend) {
        friendRepository.delete(friend);
        return true;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<FollowResponseDto> getFollowerList(Principaldetail principaldetail) {
        List<FriendDto> friendDtoList = friendRepository.getFollowerList(principaldetail.getMember().getSocialId());
        return ResponseEntity.ok().body(FollowResponseDto.builder()
                .friendDtoList(friendDtoList)
                .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK, "팔로우한 친구목록 조회에 성공하셨습니다."))
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<FollowResponseDto> getFollowingList(Principaldetail principaldetail) {
        List<FriendDto> friendDtoList = friendRepository.getFollowingList(principaldetail.getMember().getSocialId());
        return ResponseEntity.ok().body(FollowResponseDto.builder()
                .friendDtoList(friendDtoList)
                .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK, "나를 팔로우한 친구목록 조회에 성공하셨습니다."))
                .build());
    }

    public ResponseEntity<FriendResponseDto> deleteFollowingFriend(Principaldetail principaldetail, String friendId) {
        friendRepository.findByFollowerIdAndFollowingId(principaldetail.getMember().getSocialId(),friendId)
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 팔로잉을 끊으셨습니다."));
        return ResponseEntity.ok().body(FriendResponseDto.builder()
                .followCheck(false)
                .msgDto(msgUtil.getMsg(HttpServletResponse.SC_OK,"팔로잉을 끊으셨습니다."))
                .build());
    }
}
