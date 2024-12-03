package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JWTProvider {

    private final Algorithm algorithm;

    public JWTProvider(@Value("${spring.jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String createAccessToken(String kakaoMemberID) {
        Instant ACCESS_EXP = Instant.now().plusSeconds(60*10);  //10분

        String accessToken = JWT.create()
                //.withIssuer("헤어골라 서버 주소")
                .withSubject(kakaoMemberID)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(ACCESS_EXP)
                .sign(algorithm);

        return "Bearer " + accessToken;
    }

    public String createRefreshToken(String kakaoMemberID) {
        Instant REFRESH_EXP = Instant.now().plusSeconds(60*60*24*30);  //30일

        String refreshToken = JWT.create()
                //.withIssuer("헤어골라 서버 주소")
                .withSubject(kakaoMemberID)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(REFRESH_EXP)
                .sign(algorithm);

        return refreshToken;
    }


}
