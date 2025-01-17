package com.example.salonreservation.domain.member.controller;

import com.example.salonreservation.domain.member.dto.ProfileDto;
import com.example.salonreservation.domain.member.entity.RefreshToken;
import com.example.salonreservation.domain.member.service.JWTProvider;
import com.example.salonreservation.domain.member.service.KakaoLoginService;
import com.example.salonreservation.domain.member.service.MemberService;
import com.example.salonreservation.domain.member.service.RefreshTokenService;
import com.example.salonreservation.domain.member.util.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.example.salonreservation.domain.member.service.RefreshTokenService.createRefreshTokenCookie;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final KakaoLoginService kakaoLoginService;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final JWTProvider jwtProvider;

    /**
     * 카카오 로그인 페이지 리턴
     *
     * @return 카카오 계정 로그인 요청 URL
     * 프론트에서 해당 URL로 리다이렉트
     */
    @ResponseBody
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
    public String signInCallBack(@RequestParam Map<String, String> paramMap, Model model) {
        if (paramMap.containsKey("error")) {  //카카오 로그인에서 에러 발생 시
            //예외 처리
        }

        String code = paramMap.get("code");  //정상 수행 시 인가 코드 받기

        Map body = kakaoLoginService.getTokenFromKakao(code);  //카카오로부터 액세스 토큰, 리프레시 토큰, OIDC ID 토큰 받기 요청
        String accessToken = kakaoLoginService.verifyIdToken(body);  //OIDC ID 토큰 유효성 검증

        //OIDC ID 토큰 유효성 검증 성공 시 사용자 액세스 토큰으로 카카오 회원번호 조회
        String kakaoMemberId = kakaoLoginService.getKakaoMemberId(accessToken);

        memberService.ensureMemberIsJoined(kakaoMemberId);

        String refreshToken = jwtProvider.createRefreshToken(kakaoMemberId);
        refreshTokenService.addRefreshToken(new RefreshToken(kakaoMemberId, refreshToken));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie("hairgolla_refresh", refreshToken));
        model.addAttribute("accessToken", jwtProvider.createAccessToken(kakaoMemberId));
        return "redirect";
    }

    @ResponseBody
    @GetMapping("/profile")
    public ProfileDto getProfile() {
        Long memberId = SecurityContextHolder.getContext();
        return memberService.getProfile(memberId);
    }

    @ResponseBody
    @PutMapping("/profile")
    public void modifyProfile(@RequestBody ProfileDto profileDto) {
        Long memberId = SecurityContextHolder.getContext();
        memberService.modifyProfile(memberId, profileDto);
    }

    @ResponseBody
    @DeleteMapping("/profile")
    public void removeProfile() {
        Long memberId = SecurityContextHolder.getContext();
        memberService.removeProfile(memberId);
    }
}
