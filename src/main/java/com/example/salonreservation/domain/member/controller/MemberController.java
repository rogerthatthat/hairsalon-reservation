package com.example.salonreservation.domain.member.controller;

import com.example.salonreservation.domain.member.service.KakaoLoginService;
import com.example.salonreservation.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final KakaoLoginService kakaoLoginService;
    private final MemberService memberService;

    /**
     * 카카오 로그인 페이지 리턴
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
     * @param paramMap
     * @return
     */
    @GetMapping("/oauth")
    public ResponseEntity signInCallBack(@RequestParam Map<String, String> paramMap) {

        if (paramMap.containsKey("error")) {  //카카오 로그인에서 에러 발생 시
            //예외 처리
        }

        String code = paramMap.get("code");  //정상 수행 시 인가 코드 받기

        Map body = kakaoLoginService.getTokenFromKakao(code);  //카카오로부터 액세스 토큰, 리프레시 토큰, OIDC ID 토큰 받기
        String accessToken = kakaoLoginService.verifyIdToken(body);  //OIDC ID 토큰 유효성 검증

        //OIDC ID 토큰 유효성 검증 성공 시 사용자 액세스 토큰으로 카카오 회원번호 조회
        Long kakaoMemberId = kakaoLoginService.getKakaoMemberId(accessToken);

        //카카오 회원번호를 기반으로 우리 서버에 가입된 회원인지 확인
        memberService.checkMember(kakaoMemberId);

        return new ResponseEntity(body, HttpStatus.CREATED);
    }

}
