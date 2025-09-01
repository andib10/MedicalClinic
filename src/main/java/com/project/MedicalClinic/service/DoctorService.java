package com.project.MedicalClinic.service;

import com.project.MedicalClinic.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {

    List<Doctor> findAll();

    List<Doctor> findAllById(List<Integer> doctorIds);

    Page<Doctor> findAll(Pageable pageable);

    List<Doctor> findAllBySpecialtyId(int specialtyId);

    Doctor findById(int theId);

    Doctor findByUsername(String theUsername);

    Doctor save(Doctor theDoctor);
    Doctor saveNewDoc(Doctor theDoctor);

    void deleteById(int theId);
}
