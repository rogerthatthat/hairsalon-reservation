package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.salonreservation.domain.member.entity.RefreshToken;
import com.example.salonreservation.domain.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void addRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void existsRefreshToken(String refreshToken) {
        if (!refreshTokenRepository.existsById(refreshToken)) {
            throw new JWTVerificationException("Refresh Token Not Exists");
        }
    }

    @Transactional
    public void deleteByRefreshToken(String token) {
        refreshTokenRepository.deleteById(token);
    }


    public static String createRefreshTokenCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(60*60*24*30)  //30일
                .build()
                .toString();
    }


}
