package com.sparta.perdayonespoon.service;
import com.sparta.perdayonespoon.jwt.Principaldetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FriendService {
    public ResponseEntity addFriend(Principaldetail principaldetail, String friendsId) {
        return ResponseEntity.ok().body("ok");
    }

    public ResponseEntity deleteFriend(Principaldetail principaldetail, String friendId) {
        return ResponseEntity.ok().body("ok");
    }
}
