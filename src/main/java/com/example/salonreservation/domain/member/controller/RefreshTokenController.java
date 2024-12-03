package com.example.salonreservation.domain.member.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.salonreservation.domain.member.entity.RefreshToken;
import com.example.salonreservation.domain.member.service.JWTProvider;
import com.example.salonreservation.domain.member.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.salonreservation.domain.member.service.RefreshTokenService.createRefreshTokenCookie;

@RestController
public class RefreshTokenController {

    private final Algorithm algorithm;
    private final RefreshTokenService refreshTokenService;
    private final JWTProvider jwtProvider;

    public RefreshTokenController(@Value("${spring.jwt.secret}") String secret,
                                  RefreshTokenService refreshTokenService,
                                  JWTProvider jwtProvider) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.refreshTokenService = refreshTokenService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/reissue")
    public ResponseEntity reissueTokens(@CookieValue("hairgolla_refresh") String refreshToken) {
        JWTVerifier verifier = getJwtVerifier();
        DecodedJWT verifiedRefreshToken;
        String kakaoMemberId;

        try {
            verifiedRefreshToken = verifier.verify(refreshToken);
            kakaoMemberId = verifiedRefreshToken.getSubject();
            refreshTokenService.existsRefreshToken(kakaoMemberId);
        } catch (TokenExpiredException e) {
            return new ResponseEntity("Refresh Token Expired", HttpStatus.BAD_REQUEST);
        }catch (JWTVerificationException e) {
            return new ResponseEntity("Invalid Refresh Token", HttpStatus.BAD_REQUEST);
        }

        refreshTokenService.deleteByRefreshToken(refreshToken);
        String newRefreshToken = jwtProvider.createRefreshToken(kakaoMemberId);
        refreshTokenService.addRefreshToken(new RefreshToken(kakaoMemberId, newRefreshToken));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwtProvider.createAccessToken(kakaoMemberId));
        headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie("hairgolla_refresh", newRefreshToken));

        return new ResponseEntity(null, headers, HttpStatus.OK);
    }

    private JWTVerifier getJwtVerifier() {
        return JWT.require(algorithm)
                //.withIssuer("헤어골라 서버 주소")
                //the standard DateTime claims are validated by default
                .build();
    }
}
