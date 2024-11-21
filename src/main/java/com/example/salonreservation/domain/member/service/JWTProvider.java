package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.salonreservation.domain.member.entity.RefreshToken;
import com.example.salonreservation.domain.member.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private Algorithm algorithm;

    public JWTProvider(RefreshTokenRepository refreshTokenRepository, @Value("${spring.jwt.secret}") String secret) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.algorithm = Algorithm.HMAC256(secret);
    }


    public Map<String, String> createJWTs(String kakaoMemberId) {
        Map result = new HashMap();

        String accessJWT = createAccessJWT(kakaoMemberId);
        String refreshJWT = createRefreshJWT(kakaoMemberId);

        result.put("accessJWT", "Bearer " + accessJWT);
        result.put("refreshJWT", refreshJWT);

        return result;
    }


    private String createAccessJWT(String kakaoMemberID) {
        Instant ACCESS_EXP = Instant.now().plusSeconds(60*10);  //10분

        String accessJWT = JWT.create()
                //.withIssuer("헤어골라 서버 주소")
                .withSubject(kakaoMemberID)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(ACCESS_EXP)
                .sign(algorithm);

        return accessJWT;
    }

    private String createRefreshJWT(String kakaoMemberID) {
        Instant REFRESH_EXP = Instant.now().plusSeconds(60*60*24*30);  //30일

        String refreshJWT = JWT.create()
                //.withIssuer("헤어골라 서버 주소")
                .withSubject(kakaoMemberID)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(REFRESH_EXP)
                .sign(algorithm);

        RefreshToken refreshToken = new RefreshToken(kakaoMemberID, refreshJWT);
        refreshTokenRepository.save(refreshToken);

        return refreshJWT;
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
