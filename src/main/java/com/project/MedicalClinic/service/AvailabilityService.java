package com.project.MedicalClinic.service;

import com.project.MedicalClinic.entity.Availability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityService {

    List<Availability> findAll();

    Page<Availability> findAll(Pageable pageable);

    Page<Availability> findByDoctorId(int doctorId, Pageable pageable);

    Optional<Availability> findByTimeSlot(LocalDateTime timeSlot);

    public List<Availability> findAvailableTimeSlots(int doctorId);

    Availability findById(int theId);

    Availability save(Availability theAvailability);

    void saveAvailabilityForDoctors(Availability availability, List<Integer> doctorIds);

    void deleteById(int theId);
}
