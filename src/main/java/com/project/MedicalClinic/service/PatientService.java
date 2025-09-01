package com.project.MedicalClinic.service;

import com.project.MedicalClinic.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {

    List<Patient> findAll();

    Page<Patient> findAll(Pageable pageable);

    Patient findById(int theId);

    Patient findByUsername(String theUsername);

    Patient save(Patient thePatient);

    void deleteById(int theId);
}
