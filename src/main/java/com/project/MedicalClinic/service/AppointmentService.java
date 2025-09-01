package com.project.MedicalClinic.service;

import com.project.MedicalClinic.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppointmentService {

    List<Appointment> findAll();

    Page<Appointment> findAll(Pageable pageable);

    List<Appointment> findAllByDoctorId(int doctorId);

    Page<Appointment> findAllByDoctorId(int doctorId, Pageable pageable);

    Page<Appointment> findByDoctorIdAndPatientId(int doctorId, int patientId, Pageable pageable);

    List<Appointment> findAllByPatientId(int patientId);

    Page<Appointment> findAllByPatientId(int patientId, Pageable pageable);

    Page<Appointment> findByPatientIdAndDoctorSpecialtyId(int patientId, int specialtyId, Pageable pageable);
    Page<Appointment> findByPatientIdAndDoctorId(int patientId, int doctorId, Pageable pageable);
    Page<Appointment> findByPatientIdAndDoctorSpecialtyIdAndDoctorId(int patientId, int specialtyId, int doctorId, Pageable pageable);


    Appointment findById(int theId);

    Appointment save(Appointment theAppointment);

    void deleteById(int theId);
}
