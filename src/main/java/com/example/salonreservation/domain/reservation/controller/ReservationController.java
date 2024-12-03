package com.example.salonreservation.domain.reservation.controller;

import com.example.salonreservation.domain.member.util.SecurityContextHolder;
import com.example.salonreservation.domain.reservation.dto.ReservationDto;
import com.example.salonreservation.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/my-reservations")
    public List<ReservationDto> getMyReservationList() {
        Long memberId = SecurityContextHolder.getContext();
        return reservationService.getMyReservationList(memberId);
    }

    @GetMapping("/salons/{salonId}/reservations/{reservationId}")
    public ReservationDto getReservation(@PathVariable("salonId") Long salonId,
                                         @PathVariable("reservationId") Long reservationId) {
        Long memberId = SecurityContextHolder.getContext();
        return reservationService.getReservation(memberId, reservationId);
    }

    @PostMapping("/salons/{salonId}/reservations")
    public ResponseEntity addReservation(@PathVariable("salonId") Long salonId,
                                         @RequestBody ReservationDto reservationDto) {
        Long memberId = SecurityContextHolder.getContext();
        Map<String, Long> result = new HashMap<>();

        Long reservationId = reservationService.addReservation(memberId, reservationDto);
        result.put("reservationId", reservationId);
        return new ResponseEntity(reservationId, HttpStatus.CREATED);
    }

    @PutMapping("/salons/{salonId}/reservations/{reservationId}")
    public void modifyReservation(@PathVariable("salonId") Long salonId,
                                  @PathVariable("reservationId") Long reservationId,
                                  @RequestBody ReservationDto reservationDto) {
        Long memberId = SecurityContextHolder.getContext();
        reservationService.modifyReservation(memberId, reservationId, reservationDto);
    }

    @DeleteMapping("/salons/{salonId}/reservations/{reservationId}")
    public void removeReservation(@PathVariable("salonId") Long salonId,
                                  @PathVariable("reservationId") Long reservationId) {
        Long memberId = SecurityContextHolder.getContext();
        reservationService.deleteReservation(memberId, reservationId);
    }


}
