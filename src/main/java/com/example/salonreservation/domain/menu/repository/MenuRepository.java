package com.example.salonreservation.domain.menu.repository;

import com.example.salonreservation.domain.menu.entity.Menu;
import com.example.salonreservation.domain.salon.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findBySalon(Salon salon);
}
