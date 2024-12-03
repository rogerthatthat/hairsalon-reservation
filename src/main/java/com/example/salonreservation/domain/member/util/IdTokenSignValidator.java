package com.example.salonreservation.domain.member.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.salonreservation.domain.member.dto.OIDCPublicKey;
import com.example.salonreservation.domain.member.service.KakaoOIDCService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IdTokenSignValidator {

    private final KakaoOIDCService kakaoOIDCService;

    /**
     * OIDC ID 토큰 서명 유효성 검증
     * @param idToken
     * @param kid
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public void verifyIdTokenSign(DecodedJWT idToken, String kid) {
        Map<String, String> publicKey = findPublicKey(kid);
        String modulus = publicKey.get("modulus");
        String exponent = publicKey.get("exponent");

        Key rsaPublicKey = generateRSAPublicKey(modulus, exponent);

        //모듈과 지수로 생성한 공개키로 서명 유효성 검증
        JWTVerifier verifier = JWT.require(Algorithm.RSA256((RSAKey) rsaPublicKey)).build();
        verifier.verify(idToken);
    }


    /**
     * 카카오 서버가 응답한 OIDC 공개키 목록 중 ID 토큰의 kid(공개키 ID)와 일치하는 공개키 ID 찾기
     * @param kid
     * @return 해당 공개키 ID의 modulus(모듈), exponent(지수)
     */
    private Map<String, String> findPublicKey(String kid) {
        OIDCPublicKey[] publicKeyList = kakaoOIDCService.getPublicKeyList();

        //응답의 공개키 목록을 뒤져서 ID 토큰의 kid에 해당하는 values(modules, exponent로 구성) 추출
        Map<String, String> valuesForPublicKey = new HashMap<>();

        for (OIDCPublicKey key : publicKeyList) {
            if (kid.equals(key.getKid())) {
                valuesForPublicKey.put("modulus", key.getN());
                valuesForPublicKey.put("exponent", key.getE());
                break;
            }
        }

        return valuesForPublicKey;
    }


    /**
     * modulus(모듈), exponent(지수)로 공개키 생성하기
     * @param modulus
     * @param exponent
     * @return Key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * https://github.com/Gosrock/DuDoong-Backend/blob/dev/DuDoong-Common/src/main/java/band/gosrock/common/jwt/JwtOIDCProvider.java
     */
    private Key generateRSAPublicKey(String modulus, String exponent) {

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
            byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
            BigInteger n = new BigInteger(1, decodeN);
            BigInteger e = new BigInteger(1, decodeE);

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);

            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {  //예외 처리
            throw new RuntimeException(e);
        }

    }
}
