package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Patient findByUserId(int userId);
}
