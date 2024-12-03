package com.example.salonreservation.domain.menu.entity;

import com.example.salonreservation.common.entity.BaseEntity;
import com.example.salonreservation.domain.reservation.entity.Reservation;
import com.example.salonreservation.domain.salon.entity.Salon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id")
	private Long id;

	private String menuName;
	private String menuInfo;
	private String price;
	private String time;
	private boolean shampooYN;
	private boolean cutYN;
	private String target;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "salon_id", nullable = false)
	private Salon salon;

	@OneToMany(mappedBy = "menu")
	private List<Reservation> reservations = new ArrayList<>();

	@Builder
	private Menu(String menuName, String menuInfo, String price, String time, boolean shampooYN, boolean cutYN, String target, Salon salon) {
		this.menuName = menuName;
		this.menuInfo = menuInfo;
		this.price = price;
		this.time = time;
		this.shampooYN = shampooYN;
		this.cutYN = cutYN;
		this.target = target;
		this.salon = salon;
	}

	public void updateMenu(String menuName, String menuInfo, String price, String time, boolean shampooYN, boolean cutYN, String target) {
		this.menuName = menuName;
		this.menuInfo = menuInfo;
		this.price = price;
		this.time = time;
		this.shampooYN = shampooYN;
		this.cutYN = cutYN;
		this.target = target;
	}
}