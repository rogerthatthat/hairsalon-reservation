package com.example.salonreservation.domain.member.entity;

import com.example.salonreservation.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    private String kakaoMemberId;
    private String token;

    public RefreshToken(String kakaoMemberId, String token) {
        this.kakaoMemberId = kakaoMemberId;
        this.token = token;
    }
}
