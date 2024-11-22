package com.example.salonreservation.domain.reservation.service;

import com.example.salonreservation.domain.designer.entity.Designer;
import com.example.salonreservation.domain.designer.repository.DesignerRepository;
import com.example.salonreservation.domain.member.entity.Member;
import com.example.salonreservation.domain.member.repository.MemberRepository;
import com.example.salonreservation.domain.menu.entity.Menu;
import com.example.salonreservation.domain.menu.repository.MenuRepository;
import com.example.salonreservation.domain.reservation.dto.ReservationDto;
import com.example.salonreservation.domain.reservation.entity.Reservation;
import com.example.salonreservation.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final DesignerRepository designerRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<ReservationDto> getMyReservationList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();  //사용자 권한 확인
        List<Reservation> ReservationList = reservationRepository.findByMember(member);

        List<ReservationDto> result = new ArrayList<>();
        ReservationList.forEach(reservation -> result.add(ReservationDto.fromEntity(reservation)));
        return result;
    }

    @Transactional(readOnly = true)
    public ReservationDto getReservation(Long memberId, Long reservationId) {
        memberRepository.findById(memberId).orElseThrow();  //사용자 권한 확인
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        return ReservationDto.fromEntity(reservation);
    }

    public Long addReservation(Long memberId, ReservationDto reservationDto) {
        Member member = memberRepository.findById(memberId).orElseThrow();  //사용자 권한 확인
        Designer designer = designerRepository.findById(reservationDto.getDesignerId()).orElseThrow();
        Menu menu = menuRepository.findById(reservationDto.getMenuId()).orElseThrow();

        Reservation reservation = Reservation.builder()
                .serviceDate(reservationDto.getServiceDate())
                .serviceTime(reservationDto.getServiceTime())
                .member(member)
                .designer(designer)
                .menu(menu)
                .build();

        reservationRepository.save(reservation);
        return reservation.getId();
    }

    public void modifyReservation(Long memberId, Long reservationId, ReservationDto reservationDto) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        if (member.equals(reservation.getMember())) {  //사용자 권한 확인
            Designer designer = designerRepository.findById(reservationDto.getDesignerId()).orElseThrow();
            Menu menu = menuRepository.findById(reservationDto.getMenuId()).orElseThrow();
            reservation.updateReservation(reservationDto.getServiceDate(), reservationDto.getServiceTime(), designer, menu);
        } else {
            throw new RuntimeException();
        }
    }

    public void deleteReservation(Long memberId, Long reservationId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        if (member.equals(reservation.getMember())) {  //사용자 권한 확인
            reservationRepository.delete(reservation);
        } else {
            throw new RuntimeException();
        }
    }
}
