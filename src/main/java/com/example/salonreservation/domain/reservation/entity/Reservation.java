package com.example.salonreservation.domain.reservation.entity;

import com.example.salonreservation.common.entity.BaseEntity;
import com.example.salonreservation.domain.designer.entity.Designer;
import com.example.salonreservation.domain.member.entity.Member;
import com.example.salonreservation.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_id")
	private Long id;

	private LocalDateTime serviceDate;
	private LocalDateTime serviceTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "designer_id", nullable = false)
	private Designer designer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Builder
	private Reservation(LocalDateTime serviceDate, LocalDateTime serviceTime, Member member, Designer designer, Menu menu) {
		this.serviceDate = serviceDate;
		this.serviceTime = serviceTime;
		this.member = member;
		this.designer = designer;
		this.menu = menu;
	}

	public void updateReservation(LocalDateTime serviceDate, LocalDateTime serviceTime, Designer designer, Menu menu) {
		this.serviceDate = serviceDate;
		this.serviceTime = serviceTime;
		this.designer = designer;
		this.menu = menu;
	}
}