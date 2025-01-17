package com.example.salonreservation.domain.member.repository;

import com.example.salonreservation.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByKakaoMemberId(String kakaoMemberId);

}
