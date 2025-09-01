package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
}
