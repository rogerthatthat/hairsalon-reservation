package com.example.salonreservation.domain.member.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.salonreservation.domain.member.entity.RefreshToken;
import com.example.salonreservation.domain.member.repository.RefreshTokenRepository;
import com.example.salonreservation.domain.member.service.JWTProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.example.salonreservation.domain.member.service.JWTProvider.createCookie;

@RestController
public class RefreshTokenController {

    private Algorithm algorithm;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTProvider jwtProvider;

    public RefreshTokenController(@Value("${spring.jwt.secret}") String secret,
                                  RefreshTokenRepository refreshTokenRepository,
                                  JWTProvider jwtProvider) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/reissue")
    public ResponseEntity reissueTokens(@CookieValue("hairgolla_refresh") String refreshToken) {
        JWTVerifier verifier = getJwtVerifier();
        DecodedJWT verifiedRefreshToken;

        try {
            verifiedRefreshToken = verifier.verify(refreshToken);
            String kakaoMemberId = verifiedRefreshToken.getSubject();
            existsRefreshToken(kakaoMemberId);
        } catch (TokenExpiredException e) {
            return new ResponseEntity("Refresh Token Expired", HttpStatus.BAD_REQUEST);
        }catch (JWTVerificationException e) {
            return new ResponseEntity("Invalid Refresh Token", HttpStatus.BAD_REQUEST);
        }

        String kakaoMemberId = verifiedRefreshToken.getSubject();
        refreshTokenRepository.deleteById(refreshToken);
        Map<String, String> tokens = jwtProvider.createJWTs(kakaoMemberId);
        RefreshToken newRefreshToken = new RefreshToken(kakaoMemberId, tokens.get("refreshJWT"));
        refreshTokenRepository.save(newRefreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, tokens.get("accessJWT"));
        headers.add(HttpHeaders.SET_COOKIE, createCookie("hairgolla_refresh", tokens.get("refreshJWT")));

        return new ResponseEntity(null, headers, HttpStatus.OK);
    }

    private void existsRefreshToken(String refreshToken) {
        if (!refreshTokenRepository.existsById(refreshToken)) {
            throw new JWTVerificationException("Refresh Token Not Exists");
        }
    }


    private JWTVerifier getJwtVerifier() {
        return JWT.require(algorithm)
                //.withIssuer("헤어골라 서버 주소")
                //the standard DateTime claims are validated by default
                .build();
    }
}
