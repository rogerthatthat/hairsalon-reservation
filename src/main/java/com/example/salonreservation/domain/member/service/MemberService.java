package com.example.salonreservation.domain.member.service;

import com.example.salonreservation.domain.member.entity.Member;
import com.example.salonreservation.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    /**
     * 우리 서버에 가입된 회원인지 확인하기
     * @param kakaoMemberId
     */
    public Map checkMember(Long kakaoMemberId) {
        LinkedHashMap<String,Object> result = new LinkedHashMap<>();

        Optional<Member> member = memberRepository.findByKakaoMemberId(kakaoMemberId);
        if (member.isEmpty()) {
            result.put("kakaoMemberId", kakaoMemberId);
            //프론트에서 회원가입 페이지 리다이렉트

        } else {
            //사용자의 액세스 토큰, 리프레시 토큰 생성, 프론트에서 홈페이지 리다이렉트
        }

        return result;
    }

}
