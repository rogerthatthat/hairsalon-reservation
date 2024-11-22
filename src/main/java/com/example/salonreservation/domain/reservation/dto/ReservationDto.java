package com.example.salonreservation.domain.reservation.dto;

import com.example.salonreservation.domain.reservation.entity.Reservation;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationDto {
    private LocalDateTime serviceDate;
    private LocalDateTime serviceTime;
    private Long designerId;
    private Long menuId;

    public static ReservationDto fromEntity(Reservation reservation) {
        return new ReservationDto(reservation.getServiceDate(),
                reservation.getServiceTime(),
                reservation.getDesigner().getId(),
                reservation.getMenu().getId());
    }
}
