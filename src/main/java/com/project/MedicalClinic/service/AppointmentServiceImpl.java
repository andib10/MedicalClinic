package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.AppointmentRepository;
import com.project.MedicalClinic.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Page<Appointment> findAll(Pageable pageable) {
        Pageable sortedBySlotStartDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "slotStart"));
        return appointmentRepository.findAll(sortedBySlotStartDesc);
    }

    @Override
    public List<Appointment> findAllByDoctorId(int doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public Page<Appointment> findAllByDoctorId(int doctorId, Pageable pageable) {
        return appointmentRepository.findByDoctorIdOrderBySlotStartDesc(doctorId, pageable);
    }

    @Override
    public Page<Appointment> findByDoctorIdAndPatientId(int doctorId, int patientId, Pageable pageable) {
        return appointmentRepository.findByDoctorIdAndPatientId(doctorId, patientId, pageable);
    }

    @Override
    public List<Appointment> findAllByPatientId(int patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }


    @Override
    public Page<Appointment> findAllByPatientId(int patientId, Pageable pageable) {
//        return appointmentRepository.findByPatientId(patientId);
        return appointmentRepository.findByPatientIdOrderBySlotStartDesc(patientId, pageable);
    }

    @Override
    public Page<Appointment> findByPatientIdAndDoctorSpecialtyId(int patientId, int specialtyId, Pageable pageable) {
        return appointmentRepository.findByPatientIdAndDoctorSpecialtyId(patientId, specialtyId, pageable);
    }

    @Override
    public Page<Appointment> findByPatientIdAndDoctorId(int patientId, int doctorId, Pageable pageable) {
        return appointmentRepository.findByPatientIdAndDoctorId(patientId, doctorId, pageable);
    }

    @Override
    public Page<Appointment> findByPatientIdAndDoctorSpecialtyIdAndDoctorId(int patientId, int specialtyId, int doctorId, Pageable pageable) {
        return appointmentRepository.findByPatientIdAndDoctorSpecialtyIdAndDoctorId(patientId, specialtyId, doctorId, pageable);
    }


    @Override
    public Appointment findById(int theId) {
        Optional<Appointment> result = appointmentRepository.findById(theId);

        Appointment theAppointment = null;

        if (result.isPresent()) {
            theAppointment = result.get();
        }
        else {
            throw new RuntimeException("Did not find appointment id - " + theId);
        }

        return theAppointment;
    }

    @Override
    public Appointment save(Appointment theAppointment) {
        return appointmentRepository.save(theAppointment);
    }

    @Override
    public void deleteById(int theId) {
        appointmentRepository.deleteById(theId);
    }
}
