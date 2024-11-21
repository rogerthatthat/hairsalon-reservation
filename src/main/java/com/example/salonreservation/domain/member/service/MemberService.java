package com.example.salonreservation.domain.member.service;

import com.example.salonreservation.domain.member.dto.MemberDto;
import com.example.salonreservation.domain.member.entity.Member;
import com.example.salonreservation.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 헤어골라 가입 회원인지 확인
     * @param kakaoMemberId
     */
    public void ensureMemberIsJoined(String kakaoMemberId) {
        Optional<Member> member = memberRepository.findByKakaoMemberId(kakaoMemberId);

        if (member.isEmpty()) {  //미가입 회원
            Member newMember = new Member(kakaoMemberId);
            memberRepository.save(newMember);
        }

    }

    @Transactional(readOnly = true)
    public Optional<Member> getProfile(Long memberId) {
        Optional<Member> profile = memberRepository.findById(memberId);
        return profile;
    }

    public void modifyProfile(Long memberId, MemberDto memberDto) {
        Member profile = memberRepository.findById(memberId).orElseThrow();
        profile.editProfile(memberDto.getName(), memberDto.getEmail(), memberDto.getBirth(), memberDto.getGender());
    }

    public void removeProfile(Long memberId) {
        memberRepository.deleteById(memberId);
    }


}
