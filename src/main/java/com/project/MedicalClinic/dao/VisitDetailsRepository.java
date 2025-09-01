package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.VisitDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitDetailsRepository extends JpaRepository<VisitDetails, Integer> {
    Optional<VisitDetails> findByAppointmentId(int appointmentId);
    boolean existsByAppointmentId(int appointmentId);
}
