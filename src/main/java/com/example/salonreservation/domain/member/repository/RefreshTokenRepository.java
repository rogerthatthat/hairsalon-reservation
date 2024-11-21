package com.example.salonreservation.domain.member.repository;

import com.example.salonreservation.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

}
