package com.example.salonreservation.domain.member.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application-secret.yml")
public class IdTokenPayloadValidator {

    @Value("${kakao.login.token.iss}")
    private String iss;

    //ID 토큰 유효성 검증 시 대조할 임의의 문자열 -> ID 토큰 재생 공격 방지
    @Value("${kakao.login.token.nonce}")
    private String nonce;

    @Value("${kakao.rest-api-key}")
    private String restApiKey;


    /**
     * OIDC ID 토큰 페이로드 유효성 검증
     * @param idToken
     */
    public void verifyIdTokenPayload(DecodedJWT idToken) {
        verifyIss(idToken);
        verifyAud(idToken, restApiKey);
        verifyExp(idToken);
        verifyNonce(idToken, nonce);
    }


    private void verifyAud(DecodedJWT idToken, String restApiKey) {
        if (!idToken.getAudience().get(0).equals(restApiKey)) {
            throw new JWTVerificationException("Invalid Audience");
        }
    }


    private void verifyIss(DecodedJWT idToken) {
        if (!idToken.getIssuer().equals(iss)) {
            throw new JWTVerificationException("Invalid Issuer");
        }
    }


    private void verifyExp(DecodedJWT idToken) {
        if (!(idToken.getExpiresAt().getTime() > System.currentTimeMillis())) {
            throw new JWTVerificationException("Token Expired");
        }
    }


    private void verifyNonce(DecodedJWT idToken, String nonce) {
        if (!idToken.getClaim("nonce").asString().equals(nonce)) {
            throw new JWTVerificationException("Invalid Nonce");
        }
    }

}
