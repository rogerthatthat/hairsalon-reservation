package com.example.salonreservation.domain.designer.entity;

import com.example.salonreservation.common.entity.BaseEntity;
import com.example.salonreservation.domain.reservation.entity.Reservation;
import com.example.salonreservation.domain.salon.entity.Salon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Designer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "designer_id")
	private Long id;

	@Column(name = "designer_name")
	private String designerName;

	@Column(name = "designer_info")
	private String designerInfo;

	@Column(name = "work_period")
	private String workPeriod;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "salon_id", nullable = false)
	private Salon salon;

	@OneToMany(mappedBy = "designer")
	private List<Reservation> reservations = new ArrayList<>();
}