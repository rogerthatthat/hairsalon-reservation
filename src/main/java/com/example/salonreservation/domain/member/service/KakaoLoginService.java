package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
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

    @Value("${kakao.login.oidc.public-jwks-uri}")
    private String publicJwksUri;

    private final RestTemplate restTemplate;
    private final IdTokenValidator idTokenValidator;

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
     * 인가 코드를 통해 카카오 인증 서버에서 토큰 받기
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
        HttpEntity request = makeGetTokenRequest(code);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        return response.getBody();
    }


    /**
     * 카카오 인증 서버에 토큰 받기 POST 요청을 위한 Request 생성
     * @param code
     * @return 토큰 받기 POST 요청을 위한 Request
     */
    private HttpEntity makeGetTokenRequest(String code) {
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
        DecodedJWT idToken = idTokenValidator.getDecodedIdTokenFromBody(body);  //body로부터 OIDC ID 토큰 추출 후 Base64 디코딩 처리

        idTokenValidator.verifyIdTokenPayload(idToken);  //OIDC ID 토큰의 페이로드 유효성 검증

        Claim kidFromIdToken = idTokenValidator.getKidFromIdToken(idToken);  //OIDC ID 토큰의 kid값 추출
        Map valuesForPublicKey = getPublicKey(kidFromIdToken);  //공개키 목록에서 OIDC ID 토큰의 kid에 해당하는 공개키 값들 추출

        idTokenValidator.verifyIdTokenSign(valuesForPublicKey);  //OIDC ID 토큰의 서명 유효성 검증

        return idTokenValidator.getAccessTokenFromBody(body);
    }


    private Map getPublicKey(Claim kid) {
        ResponseEntity<Object> response = restTemplate.getForEntity(publicJwksUri, Object.class);
        response.getBody();

        //공개 키 캐싱

        //응답의 공개키 목록을 뒤져서 ID 토큰의 kid에 해당하는 values(modules, exponent로 구성) 추출
        Map<String, String> valuesForPublicKey = new HashMap<>();
        valuesForPublicKey.put("modules", "");
        valuesForPublicKey.put("exponent", "");

        return valuesForPublicKey;
    }


    /**
     * 회원의 카카오 accessToken으로 회원의 카카오 회원번호 불러오기
     * @param accessToken
     * @return kakaoMemberId
     */
    public Long getKakaoMemberId(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, Map.class);
        Long kakaoMemberId = (Long) response.getBody().get("sub");

        return kakaoMemberId;
    }


}
