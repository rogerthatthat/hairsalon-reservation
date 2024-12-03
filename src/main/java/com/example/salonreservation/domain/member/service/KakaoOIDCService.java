package com.example.salonreservation.domain.member.service;

import com.example.salonreservation.domain.member.dto.OIDCPublicKey;
import com.example.salonreservation.domain.member.dto.OIDCPublicKeysResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:application-secret.yml")
public class KakaoOIDCService {

    private final RestTemplate restTemplate;

    @Value("${kakao.login.oidc.public-jwks-uri}")
    private String publicJwksUri;

    /**
     * 카카오 서버로부터 OIDC 공개키 목록 조회하기
     * 지나친 조회 요청 시, 요청이 차단될 수 있으므로 Redis에 캐싱
     * @return OIDCPublicKey[]
     */
    @Cacheable(value = "keys", cacheManager = "redisCacheManager", key = "#root.methodName")
    public OIDCPublicKey[] getPublicKeyList() {

        return restTemplate.getForObject(publicJwksUri, OIDCPublicKeysResponse.class).getKeys();
    }
}
