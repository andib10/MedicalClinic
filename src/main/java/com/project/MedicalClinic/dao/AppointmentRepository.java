package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository  extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByDoctorId(int doctorId);

    Page<Appointment> findByDoctorIdOrderBySlotStartDesc(int doctorId, Pageable pageable);

    Page<Appointment> findByDoctorIdAndPatientId(int doctorId, int patientId, Pageable pageable);

    List<Appointment> findByPatientId(int patientId);

    Page<Appointment> findByPatientIdOrderBySlotStartDesc(int patientId, Pageable pageable);

    Page<Appointment> findByPatientIdAndDoctorSpecialtyId(int patientId, int specialtyId, Pageable pageable);
    Page<Appointment> findByPatientIdAndDoctorId(int patientId, int doctorId, Pageable pageable);
    Page<Appointment> findByPatientIdAndDoctorSpecialtyIdAndDoctorId(int patientId, int specialtyId, int doctorId, Pageable pageable);
}
