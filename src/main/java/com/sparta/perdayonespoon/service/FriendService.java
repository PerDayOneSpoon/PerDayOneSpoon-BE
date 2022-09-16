package com.sparta.perdayonespoon.service;
import com.sparta.perdayonespoon.domain.Friend;
import com.sparta.perdayonespoon.domain.Member;
import com.sparta.perdayonespoon.domain.dto.response.FriendResponseDto;
import com.sparta.perdayonespoon.domain.follow.FollowResponseDto;
import com.sparta.perdayonespoon.domain.follow.FriendDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.FriendRepository;
import com.sparta.perdayonespoon.repository.MemberRepository;
import com.sparta.perdayonespoon.util.GenerateMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FriendService {
    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    public ResponseEntity addFriend(Principaldetail principaldetail, String friendId) {
        if(principaldetail.getMember().getSocialId().equals(friendId)){
            throw new IllegalArgumentException("자신은 팔로우 할 수 없습니다.");
        }
        if(isFollowBetween(friendId,principaldetail.getMember().getSocialId())){
            throw new IllegalArgumentException("이미 팔로우를 신청하셨습니다.");
        }
        Member member = memberRepository.findBySocialId(friendId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Friend friend = Friend.builder().followerId(member.getSocialId()).followingId(principaldetail.getMember().getSocialId()).build();
        friendRepository.save(friend);
        FriendResponseDto friendResponseDto = FriendResponseDto.builder()
                .followCheck(true)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"팔로우를 신청하셨습니다."))
                .build();
        return ResponseEntity.ok().body(friendResponseDto);
    }

    public ResponseEntity deleteFriend(Principaldetail principaldetail, String friendId) {
        friendRepository.findByFollowerIdAndFollowingId(friendId,principaldetail.getMember().getSocialId())
                .map(this::delete)
                .orElseThrow(() -> new IllegalArgumentException("이미 팔로우를 취소하셨습니다."));
        FriendResponseDto friendResponseDto = FriendResponseDto.builder()
                .followCheck(false)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK,"팔로우를 끊으셨습니다."))
                .build();
        return ResponseEntity.ok().body(friendResponseDto);
    }

    public boolean isFollowBetween(String user, String checkUser) {
        return friendRepository.findByFollowerIdAndFollowingId(user, checkUser).isPresent();
    }

    private boolean delete(Friend friend) {
        friendRepository.delete(friend);
        return true;
    }

    public ResponseEntity getFollowerList(Principaldetail principaldetail) {
        List<FriendDto> friendDtoList = friendRepository.getFollowerList(principaldetail.getMember().getSocialId());
        return ResponseEntity.ok().body(FollowResponseDto.builder()
                .friendDtoList(friendDtoList)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "팔로우한 친구목록 조회에 성공하셨습니다."))
                .build());
    }

    public ResponseEntity getFollowingList(Principaldetail principaldetail) {
        List<FriendDto> friendDtoList = friendRepository.getFollowingList(principaldetail.getMember().getSocialId());
        return ResponseEntity.ok().body(FollowResponseDto.builder()
                .friendDtoList(friendDtoList)
                .msgDto(GenerateMsg.getMsg(HttpServletResponse.SC_OK, "나를 팔로우한 친구목록 조회에 성공하셨습니다."))
                .build());
    }
}
