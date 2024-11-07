package com.example.salonreservation.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenHelper {

    public DecodedJWT getDecodedIdTokenFromBody(Map body) {
        String idToken = (String) body.get("id_token");
        return JWT.decode(idToken);  //Base64 디코딩
    }

    public String getAccessTokenFromBody(Map body) {
        return (String) body.get("access_token");
    }

    public String getKidFromIdToken(DecodedJWT idToken) {
        return idToken.getHeaderClaim("kid").asString();
    }

    public Long getKakaoMemberIdFromResponse(ResponseEntity<Map> response) {
        return Long.valueOf((String) response.getBody().get("sub"));
    }

}
