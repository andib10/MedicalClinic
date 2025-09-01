package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    List<Doctor> findBySpecialtyId(int specialtyId);

    Doctor findByUserId(int userId);
}
