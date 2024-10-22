package com.example.salonreservation.domain.member.entity;

import com.example.salonreservation.common.entity.BaseEntity;
import com.example.salonreservation.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String name;
	private String email;
	private String birth;
	private String gender;

	@OneToMany(mappedBy = "member")
	private List<Reservation> reservations = new ArrayList<>();
}
