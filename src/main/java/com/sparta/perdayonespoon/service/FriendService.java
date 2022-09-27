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
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendService {

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
        Member member = memberRepository.findBySocialId(friendId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Friend friend = Friend.builder().followerId(member.getSocialId()).followingId(principaldetail.getMember().getSocialId()).build();
        friendRepository.save(friend);
        // 인싸 뱃지를 구하기 위한 로직
        if(!member.getBadgeList().isEmpty()){
            if(member.getBadgeList().stream().noneMatch(badge -> badge.getBadgeName().equals("인싸 뱃지"))){
                List<Friend> friendList = friendRepository.getBothFollow(principaldetail.getMember().getSocialId());
                List<String> followerFriendList = friendList.stream()
                        .filter(f->f.getFollowerId().equals(principaldetail.getMember().getSocialId()))
                        .map(Friend::getFollowingId)
                        .collect(Collectors.toList());
                int standardNumber = followerFriendList.size();
                int friendNumber =0;
                if(standardNumber>4) {
                    Set<String> followingFriendList = friendList.stream()
                            .filter(f -> f.getFollowingId().equals(principaldetail.getMember().getSocialId()))
                            .map(Friend::getFollowerId)
                            .collect(Collectors.toSet());
                    for(int i=0; i<standardNumber; i++){
                        if(followingFriendList.contains(followerFriendList.get(0))){
                            friendNumber++;
                            if(friendNumber>4)
                                break;
                        }
                    }
                    if(friendNumber == 5){
                        badgeRepository.save(Badge
                                .builder()
                                .badgeName("인싸 뱃지")
                                .member(member)
                                .createdAt(LocalDateTime.now().toLocalDate())
                                .badgeNumber(6)
                                .build());
                    }
                }
            }
        }

        return ResponseEntity.ok().body(FriendResponseDto.builder()
                .followCheck(true)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"팔로우를 신청하셨습니다."))
                .build());
    }

    public ResponseEntity<FriendResponseDto> deleteFollowerFriend(Principaldetail principaldetail, String friendId) {
        friendRepository.findByFollowerIdAndFollowingId(friendId,principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 팔로우를 취소하셨습니다."));
        return ResponseEntity.ok().body(FriendResponseDto.builder()
                .followCheck(false)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"팔로우를 끊으셨습니다."))
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
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "팔로우한 친구목록 조회에 성공하셨습니다."))
                .build());
    }

    @Transactional(readOnly = true)
    public ResponseEntity<FollowResponseDto> getFollowingList(Principaldetail principaldetail) {
        List<FriendDto> friendDtoList = friendRepository.getFollowingList(principaldetail.getMember().getSocialId());
        return ResponseEntity.ok().body(FollowResponseDto.builder()
                .friendDtoList(friendDtoList)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "나를 팔로우한 친구목록 조회에 성공하셨습니다."))
                .build());
    }

    public ResponseEntity<FriendResponseDto> deleteFollowingFriend(Principaldetail principaldetail, String friendId) {
        friendRepository.findByFollowerIdAndFollowingId(principaldetail.getMember().getSocialId(),friendId)
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 팔로잉을 끊으셨습니다."));
        return ResponseEntity.ok().body(FriendResponseDto.builder()
                .followCheck(false)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"팔로잉을 끊으셨습니다."))
                .build());
    }
}
