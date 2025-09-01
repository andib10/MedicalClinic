package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.Availability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    List<Availability> findByDoctorsId(int doctorId);

    Page<Availability> findByDoctorsIdOrderByTimeSlotDesc(int doctorId, Pageable pageable);

    Optional<Availability> findByTimeSlot(LocalDateTime timeSlot);
}
