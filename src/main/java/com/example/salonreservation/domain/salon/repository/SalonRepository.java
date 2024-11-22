package com.example.salonreservation.domain.salon.repository;

import com.example.salonreservation.domain.salon.entity.Salon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {
}
