package com.example.salonreservation.domain.member.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.salonreservation.domain.member.repository.MemberRepository;
import com.example.salonreservation.domain.member.util.SecurityContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Component
public class JWTValidator extends OncePerRequestFilter {

    private final Algorithm algorithm;
    private final SecurityContextHolder securityContextHolder;
    private final MemberRepository memberRepository;
    private static final List<String> EXCLUDE_URLS = List.of("/signin", "/oauth", "/reissue");

    public JWTValidator(@Value("${spring.jwt.secret}") String secret, SecurityContextHolder securityContextHolder, MemberRepository memberRepository) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.securityContextHolder = securityContextHolder;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!isBearerTokenType(authorization)) return;

        String accessToken = getAccessToken(authorization);
        JWTVerifier verifier = getJwtVerifier();

        try {
            DecodedJWT verifiedAccessToken = verifier.verify(accessToken);

            String kakaoMemberId = verifiedAccessToken.getSubject();
            Long memberId = memberRepository.findByKakaoMemberId(kakaoMemberId).orElseThrow().getId();
            securityContextHolder.setContext(memberId);

            filterChain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            PrintWriter writer = response.getWriter();
            writer.print("Access Token Expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //프론트에서 /reissue로 Redirect
        } catch (JWTVerificationException e) {
            PrintWriter writer = response.getWriter();
            writer.print("Invalid Access Token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            securityContextHolder.clearContext();
        }

    }

    private static boolean isBearerTokenType(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer")) {
            return false;
        }
        return true;
    }

    private static String getAccessToken(String authorization) {
        return authorization.split(" ")[1];
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URLS.stream().anyMatch(url -> url.equals(request.getRequestURI()));
    }

    private JWTVerifier getJwtVerifier() {
        return JWT.require(algorithm)
                //.withIssuer("헤어골라 서버 주소")
                //the standard DateTime claims are validated by default
                .build();

    }





}
