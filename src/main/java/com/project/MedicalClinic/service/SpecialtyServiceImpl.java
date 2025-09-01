package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.SpecialtyRepository;
import com.project.MedicalClinic.entity.Specialty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialtyServiceImpl implements SpecialtyService{

    private SpecialtyRepository specialtyRepository;

    @Autowired
    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public List<Specialty> findAll() {
        return specialtyRepository.findAll();
    }

    @Override
    public Specialty findById(int theId) {
        Optional<Specialty> result = specialtyRepository.findById(theId);

        Specialty theSpecialty = null;

        if (result.isPresent()) {
            theSpecialty = result.get();
        }
        else {
            throw new RuntimeException("Did not find specialty id - " + theId);
        }

        return theSpecialty;
    }

    @Override
    public Specialty save(Specialty theSpecialty) {
        return specialtyRepository.save(theSpecialty);
    }

    @Override
    public void deleteById(int theId) {
        specialtyRepository.deleteById(theId);
    }
}
