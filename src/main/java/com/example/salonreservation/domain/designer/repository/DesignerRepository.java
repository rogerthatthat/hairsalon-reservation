package com.example.salonreservation.domain.designer.repository;

import com.example.salonreservation.domain.designer.entity.Designer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignerRepository extends JpaRepository<Designer, Long> {
}
