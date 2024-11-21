package com.example.salonreservation.domain.member.controller;

import com.example.salonreservation.domain.member.dto.MemberDto;
import com.example.salonreservation.domain.member.entity.Member;
import com.example.salonreservation.domain.member.service.JWTProvider;
import com.example.salonreservation.domain.member.service.KakaoLoginService;
import com.example.salonreservation.domain.member.service.MemberService;
import com.example.salonreservation.domain.member.util.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static com.example.salonreservation.domain.member.service.JWTProvider.createCookie;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final KakaoLoginService kakaoLoginService;
    private final MemberService memberService;
    private final JWTProvider jwtProvider;
    private final SecurityContextHolder securityContextHolder;

    /**
     * 카카오 로그인 페이지 리턴
     *
     * @return 카카오 계정 로그인 요청 URL
     * 프론트에서 해당 URL로 리다이렉트
     */
    @GetMapping("/signin")
    public ResponseEntity getSignInPage() {
        Map body = kakaoLoginService.getKakaoSignInPage();

        return new ResponseEntity(body, HttpStatus.OK);
    }


    /**
     * 프론트에서 사용자의 카카오 로그인 후 카카오 인증 서버가 보내온 인가 코드 받기
     * 이후 카카오로부터 액세스 토큰, 리프레시 토큰, OIDC ID 토큰 받기 요청
     * OIDC ID 토큰 유효성 검증
     * 회원의 헤어골라 가입여부 확인
     *
     * @param paramMap
     * @return
     */
    @GetMapping("/oauth")
    public ResponseEntity signInCallBack(@RequestParam Map<String, String> paramMap) {
        if (paramMap.containsKey("error")) {  //카카오 로그인에서 에러 발생 시
            //예외 처리
        }

        String code = paramMap.get("code");  //정상 수행 시 인가 코드 받기

        Map body = kakaoLoginService.getTokenFromKakao(code);  //카카오로부터 액세스 토큰, 리프레시 토큰, OIDC ID 토큰 받기 요청
        String accessToken = kakaoLoginService.verifyIdToken(body);  //OIDC ID 토큰 유효성 검증

        //OIDC ID 토큰 유효성 검증 성공 시 사용자 액세스 토큰으로 카카오 회원번호 조회
        String kakaoMemberId = kakaoLoginService.getKakaoMemberId(accessToken);

        memberService.ensureMemberIsJoined(kakaoMemberId);
        Map<String, String> jwTs = jwtProvider.createJWTs(kakaoMemberId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwTs.get("accessJWT"));
        headers.add(HttpHeaders.SET_COOKIE, createCookie("hairgolla_refresh", jwTs.get("refreshJWT")));
        headers.add(HttpHeaders.LOCATION, "/");

        return new ResponseEntity(null, headers, HttpStatus.FOUND);
    }


    @GetMapping("/profile")
    public Optional<Member> getProfile() {
        Long memberId = securityContextHolder.getContext();
        Optional<Member> profile = memberService.getProfile(memberId);
        System.out.println("memberId = " + memberId);

        return profile;
    }


    @PutMapping("/profile")
    public void modifyProfile(@RequestBody MemberDto memberDto) {
        Long memberId = securityContextHolder.getContext();
        System.out.println("memberId = " + memberId);
        memberService.modifyProfile(memberId, memberDto);
    }


    @DeleteMapping("/profile")
    public void removeProfile() {
        Long memberId = securityContextHolder.getContext();
        System.out.println("memberId = " + memberId);
        memberService.removeProfile(memberId);
    }
}
