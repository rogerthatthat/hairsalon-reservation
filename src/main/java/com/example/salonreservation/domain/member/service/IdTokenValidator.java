package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Map;

/**
 * OIDC ID 토큰 유효성 검증
 */
@Component
public class IdTokenValidator {

    private static final String ISS_VALUE = "https://kauth.kakao.com";

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


    /**
     * OIDC ID 토큰 서명 유효성 검증
     * @param valuesForPublicKey
     */
    public void verifyIdTokenSign(Map<String, String> valuesForPublicKey) {
        String modules = valuesForPublicKey.get("modules");
        String exponent = valuesForPublicKey.get("exponent");
        Key rsaPublicKey = getRSAPublicKey(modules, exponent);

        //rsaPublicKey 가지고 서명 유효성 검증
    }


    private void verifyAud(DecodedJWT idToken, String restApiKey) {
        if (!idToken.getAudience().get(0).equals(restApiKey)) {
            throw new JWTVerificationException("토큰의 수신자가 일치하지 않습니다.");
        }
    }


    private void verifyIss(DecodedJWT idToken) {
        if (!idToken.getIssuer().equals(ISS_VALUE)) {
            throw new JWTVerificationException("토큰의 발급자가 일치하지 않습니다.");
        }
    }


    private void verifyExp(DecodedJWT idToken) {
        if (!(idToken.getExpiresAt().getTime() > System.currentTimeMillis())) {
            throw new JWTVerificationException("토큰이 만료되었습니다.");
        }
    }


    private void verifyNonce(DecodedJWT idToken, String nonce) {
        if (!idToken.getClaim("nonce").asString().equals(nonce)) {
            throw new JWTVerificationException("토큰의 nonce 값이 일치하지 않습니다.");
        }
    }


    public DecodedJWT getDecodedIdTokenFromBody(Map body) {
        String idToken = (String) body.get("id_token");
        return JWT.decode(idToken);  //Base64 디코딩
    }


    public String getAccessTokenFromBody(Map body) {
        return (String) body.get("access_token");
    }


    public Claim getKidFromIdToken(DecodedJWT idToken) {
        return idToken.getHeaderClaim("kid");
    }


    public Key getRSAPublicKey(String modules, String exponent) {
        //modules와 exponent를 구성하여 OIDC 공개키 생성
        return null;
    }

}
