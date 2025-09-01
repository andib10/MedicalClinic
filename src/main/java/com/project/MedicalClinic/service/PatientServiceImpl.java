package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.PatientRepository;
import com.project.MedicalClinic.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService{

    private PatientRepository patientRepository;
    private EntityManager entityManager;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, EntityManager entityManager) {
        this.patientRepository = patientRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public Page<Patient> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    @Override
    public Patient findById(int theId) {
        Optional<Patient> result = patientRepository.findById(theId);

        Patient thePatient = null;

        if (result.isPresent()) {
            thePatient = result.get();
        }
        else {
            throw new RuntimeException("Did not find patient id - " + theId);
        }

        return thePatient;
    }

    @Override
    public Patient findByUsername(String theUsername) {
        TypedQuery<Patient> theQuery = entityManager.createQuery(
                "SELECT p FROM Patient p JOIN p.user u WHERE u.userName = :uName", Patient.class);
        theQuery.setParameter("uName", theUsername);
        Patient thePatient = null;
        try {
            thePatient = theQuery.getSingleResult();
        } catch (Exception e) {
            thePatient = null;
        }

        return thePatient;
    }

    @Override
    public Patient save(Patient thePatient) {

        Patient existingPatient = patientRepository.findById(thePatient.getId()).orElse(null);

        assert existingPatient != null;
        existingPatient.setBirthDate(thePatient.getBirthDate());
        existingPatient.setCNP(thePatient.getCNP());
        existingPatient.setPhoneNumber(thePatient.getPhoneNumber());
        existingPatient.setUser(existingPatient.getUser()); // able to update the Patient without losing the relationship with the User

        return patientRepository.save(existingPatient);
    }

    @Override
    public void deleteById(int theId) {
        patientRepository.deleteById(theId);
    }
}
