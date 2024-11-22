package com.example.salonreservation.domain.reservation.repository;

import com.example.salonreservation.domain.member.entity.Member;
import com.example.salonreservation.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByMember(Member member);
}
