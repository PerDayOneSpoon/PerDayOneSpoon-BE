package com.sparta.perdayonespoon.jwt;


import com.sparta.perdayonespoon.domain.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class Principaldetail implements UserDetails {
    private final Member member;

    public Member getMember(){
        return member;
    }

    public Principaldetail(Member member){
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority>collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getAuthority().toString();
            }
        });
        return collection;
    }

    @Override
    public String getPassword(){
        return this.member.getPassword();
    }
    @Override
    public String getUsername(){
        return this.member.getNickname();
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }
}
