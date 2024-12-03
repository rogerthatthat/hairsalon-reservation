package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.salonreservation.domain.member.util.IdTokenPayloadValidator;
import com.example.salonreservation.domain.member.util.IdTokenSignValidator;
import com.example.salonreservation.domain.member.util.TokenHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-secret.yml")
public class KakaoLoginService {

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    @Value("${kakao.login.authorize.uri}")
    private String authorizeUri;

    @Value("${kakao.login.authorize.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.login.token.uri}")
    private String tokenUri;

    //ID 토큰 유효성 검증 시 대조할 임의의 문자열 -> ID 토큰 재생 공격 방지
    @Value("${kakao.login.token.nonce}")
    private String nonce;

    @Value("${kakao.login.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;
    private final TokenHelper tokenHelper;
    private final IdTokenPayloadValidator idTokenPayloadValidator;
    private final IdTokenSignValidator idTokenSignValidator;

    /**
     * 카카오 로그인 페이지 주소 생성
     * @return 카카오 로그인 페이지 주소 (프론트가 리다이렉트할 주소)
     */
    public Map getKakaoSignInPage() {
        LinkedHashMap<String,Object> result = new LinkedHashMap<>();

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl(authorizeUri)
                .queryParam("client_id", restApiKey)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("nonce", nonce)
                .build()
                .toUriString();

        result.put("signInPage", redirectUrl);

        return result;
    }


    /**
     * 인가 코드를 통해 카카오 인증 서버에서 토큰 받기 요청
     * @param code
     * @return 액세스 토큰, 리프레시 토큰, OIDC ID 토큰
     *          {
     *     "token_type": "bearer",
     *     "access_token": "${ACCESS_TOKEN}",
     *     "id_token": "${ID_TOKEN}",
     *     "expires_in": 7199,
     *     "refresh_token": "${REFRESH_TOKEN}",
     *     "refresh_token_expires_in": 86399,
     * }
     */
    public Map getTokenFromKakao(String code) {
        HttpEntity request = generateGetTokenRequest(code);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        return response.getBody();
    }


    /**
     * 카카오 인증 서버에 토큰 받기 POST 요청을 위한 Request 생성
     * @param code
     * @return 토큰 받기 POST 요청을 위한 Request
     */
    private HttpEntity generateGetTokenRequest(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", restApiKey);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<Map>(params, headers);
    }


    /**
     * 회원의 OIDC ID 토큰 유효성 검증
     * @param body
     * @return
     */
    public String verifyIdToken(Map body) {
        DecodedJWT idToken = tokenHelper.getDecodedIdTokenFromBody(body);  //body로부터 OIDC ID 토큰 추출 후 Base64 디코딩 처리
        idTokenPayloadValidator.verifyIdTokenPayload(idToken);  //OIDC ID 토큰의 페이로드 유효성 검증

        String kid = tokenHelper.getKidFromIdToken(idToken);  //OIDC ID 토큰의 kid값 추출
        idTokenSignValidator.verifyIdTokenSign(idToken, kid);  //OIDC ID 토큰의 서명 유효성 검증

        return tokenHelper.getAccessTokenFromBody(body);
    }


    /**
     * 회원의 카카오 accessToken으로 회원의 카카오 회원번호 불러오기
     * @param accessToken
     * @return kakaoMemberId
     */
    public String getKakaoMemberId(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, Map.class);

        return getKakaoMemberIdFromResponse(response);
    }

    private String getKakaoMemberIdFromResponse(ResponseEntity<Map> response) {
        return (String) response.getBody().get("sub");
    }
}
