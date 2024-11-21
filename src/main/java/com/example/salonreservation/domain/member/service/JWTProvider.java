package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.salonreservation.domain.member.entity.RefreshToken;
import com.example.salonreservation.domain.member.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Algorithm algorithm;

    public JWTProvider(RefreshTokenRepository refreshTokenRepository, @Value("${spring.jwt.secret}") String secret) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.algorithm = Algorithm.HMAC256(secret);
    }


    public Map<String, String> createTokens(String kakaoMemberId) {
        Map tokens = new HashMap();

        String accessToken = createAccessToken(kakaoMemberId);
        String refreshToken = createRefreshToken(kakaoMemberId);

        tokens.put("accessToken", "Bearer " + accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }


    private String createAccessToken(String kakaoMemberID) {
        Instant ACCESS_EXP = Instant.now().plusSeconds(60*10);  //10분

        String accessToken = JWT.create()
                //.withIssuer("헤어골라 서버 주소")
                .withSubject(kakaoMemberID)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(ACCESS_EXP)
                .sign(algorithm);

        return accessToken;
    }

    @Transactional
    private String createRefreshToken(String kakaoMemberID) {
        Instant REFRESH_EXP = Instant.now().plusSeconds(60*60*24*30);  //30일

        String refreshToken = JWT.create()
                //.withIssuer("헤어골라 서버 주소")
                .withSubject(kakaoMemberID)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(REFRESH_EXP)
                .sign(algorithm);

        refreshTokenRepository.save(new RefreshToken(kakaoMemberID, refreshToken));
        return refreshToken;
    }

    public static String createCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .maxAge(60*60*24*30)  //30일
                .build()
                .toString();
    }


}
