package com.example.salonreservation.domain.salon.entity;

import com.example.salonreservation.common.entity.BaseEntity;
import com.example.salonreservation.domain.designer.entity.Designer;
import com.example.salonreservation.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Salon extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salon_id")
	private Long id;
	private String password;

	private String salonName;
	private String salonInfo;
	private double latitude; // 위도
	private double longitude; // 경도
	private String address;
	private String phoneNum;
	private int likes; // 좋아요 수

	@OneToMany(mappedBy = "salon")
	private List<Designer> designers = new ArrayList<>();

	@OneToMany(mappedBy = "salon")
	private List<Menu> menus = new ArrayList<>();
}