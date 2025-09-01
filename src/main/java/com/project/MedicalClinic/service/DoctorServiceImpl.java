package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.DoctorRepository;
import com.project.MedicalClinic.entity.Doctor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class DoctorServiceImpl implements DoctorService{

    private Logger logger = Logger.getLogger(getClass().getName());

    private EntityManager entityManager;

    private DoctorRepository doctorRepository;

    private UserService userService;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository, EntityManager entityManager, UserService userService) {
        this.doctorRepository = doctorRepository;
        this.entityManager = entityManager;
        this.userService = userService;
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public List<Doctor> findAllById(List<Integer> doctorIds) {
        return doctorRepository.findAllById(doctorIds);
    }

    @Override
    public Page<Doctor> findAll(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    @Override
    public List<Doctor> findAllBySpecialtyId(int specialtyId) {
        return doctorRepository.findBySpecialtyId(specialtyId);
    }

    @Override
    public Doctor findById(int theId) {
        Optional<Doctor> result = doctorRepository.findById(theId);

        Doctor theDoctor = null;

        if (result.isPresent()) {
            theDoctor = result.get();
        }
        else {
            throw new RuntimeException("Did not find doctor id - " + theId);
        }

        return theDoctor;
    }

    @Override
    public Doctor findByUsername(String theUsername) {
        TypedQuery<Doctor> theQuery = entityManager.createQuery(
                "SELECT d FROM Doctor d JOIN d.user u WHERE u.userName = :uName", Doctor.class);
        theQuery.setParameter("uName", theUsername);
        Doctor theDoctor = null;
        try {
            theDoctor = theQuery.getSingleResult();
        } catch (Exception e) {
            theDoctor = null;
        }

        return theDoctor;
    }

    @Override
    public Doctor save(Doctor theDoctor) {

        Doctor existingDoctor = doctorRepository.findById(theDoctor.getId()).orElse(null);

        logger.info("DOC USER = " + existingDoctor.getUser());

        existingDoctor.setHiringDate(theDoctor.getHiringDate());
        existingDoctor.setSpecialty(theDoctor.getSpecialty());
        existingDoctor.setUser(existingDoctor.getUser()); //  able to update the Doctor without losing the relationship with the User

        return doctorRepository.save(existingDoctor);
    }

    @Override
    public Doctor saveNewDoc(Doctor theDoctor) {

        return doctorRepository.save(theDoctor);
    }

    @Override
    public void deleteById(int theId) {
        doctorRepository.deleteById(theId);
    }
}
