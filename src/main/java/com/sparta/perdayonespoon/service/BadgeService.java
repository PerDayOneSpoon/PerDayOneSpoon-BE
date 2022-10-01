package com.sparta.perdayonespoon.service;

import com.sparta.perdayonespoon.domain.Badge;
import com.sparta.perdayonespoon.domain.dto.response.MsgDto;
import com.sparta.perdayonespoon.domain.dto.response.badge.BadgeListDto;
import com.sparta.perdayonespoon.domain.dto.response.badge.BadgeResponseDto;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import com.sparta.perdayonespoon.repository.BadgeRepository;
import com.sparta.perdayonespoon.util.BadgeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BadgeService {
    private final BadgeUtil badgeUtil;
    private final BadgeRepository badgeRepository;

    public ResponseEntity<BadgeListDto> checkAllBadge(Principaldetail principaldetail){
        List<Badge> badgeList = badgeRepository.findAllByMember_Id(principaldetail.getMember().getId());
        int badgeSize = badgeList.size();
        Map<Integer, Integer> badgeNumberMap = new HashMap<>();
        List<BadgeResponseDto> badgeResponseDtoList = new ArrayList<>();
        int totalBadgeSize = 17;
        if(badgeSize==0){
            for(int i=1; i<=totalBadgeSize; i++){
                badgeResponseDtoList.add(BadgeResponseDto.formBadgeBuilder()
                        .badgeInfo(badgeUtil.getBadgeExplain(i))
                        .badgeName(badgeUtil.getBadgeName(i))
                        .badgeUrl(badgeUtil.getBadgeUrl(18))
                        .badgeNumber(i)
                        .build());
            }
        }else {
            for(int i=0; i<badgeSize; i++){
                badgeNumberMap.put((int) badgeList.get(i).getBadgeNumber(),i);
            }
            for(int i=1; i<=totalBadgeSize; i++){
                if(badgeNumberMap.containsKey(i)){
                    badgeResponseDtoList.add(BadgeResponseDto.memberBadgeBuilder()
                            .badgeNumber(badgeList.get(badgeNumberMap.get(i)).getBadgeNumber())
                            .badgeName(badgeList.get(badgeNumberMap.get(i)).getBadgeName())
                            .createdAt(badgeList.get(badgeNumberMap.get(i)).getCreatedAt())
                            .badgeInfo(badgeUtil.getBadgeExplain((int) badgeList.get(badgeNumberMap.get(i)).getBadgeNumber()))
                            .badgeUrl(badgeUtil.getBadgeUrl((int) badgeList.get(badgeNumberMap.get(i)).getBadgeNumber()))
                            .build());
                }else {
                    badgeResponseDtoList.add(BadgeResponseDto.formBadgeBuilder()
                            .badgeInfo(badgeUtil.getBadgeExplain(i))
                            .badgeName(badgeUtil.getBadgeName(i))
                            .badgeUrl(badgeUtil.getBadgeUrl(18))
                            .badgeNumber(i)
                            .build());
                }
            }
        }
        return ResponseEntity.ok().body(BadgeListDto.builder()
                .badgeResponseDtoList(badgeResponseDtoList)
                .msgDto(MsgDto.builder().code(HttpServletResponse.SC_OK).msg("뱃지 리스트 조회에 성공하셨습니다.").build())
                .build()
        );
    }

}
