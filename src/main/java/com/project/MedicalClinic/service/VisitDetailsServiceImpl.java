package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.VisitDetailsRepository;
import com.project.MedicalClinic.entity.VisitDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitDetailsServiceImpl implements VisitDetailsService{

    private VisitDetailsRepository visitDetailsRepository;

    @Autowired
    public VisitDetailsServiceImpl(VisitDetailsRepository visitDetailsRepository) {
        this.visitDetailsRepository = visitDetailsRepository;
    }

    @Override
    public List<VisitDetails> findAll() {
        return visitDetailsRepository.findAll();
    }

    @Override
    public Page<VisitDetails> findAll(Pageable pageable) {
        return visitDetailsRepository.findAll(pageable);
    }

    @Override
    public boolean existsByAppointmentId(int appointmentId) {
        return visitDetailsRepository.existsByAppointmentId(appointmentId);
    }

    @Override
    public VisitDetails findById(int theId) {
        Optional<VisitDetails> result = visitDetailsRepository.findById(theId);

        VisitDetails theVisit = null;

        if (result.isPresent()) {
            theVisit = result.get();
        }
        else {
            throw new RuntimeException("Did not find visit details id - " + theId);
        }

        return theVisit;
    }

    @Override
    public Optional<VisitDetails> findByAppointmentId(int appointmentId) {
        return visitDetailsRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public VisitDetails save(VisitDetails theVisit) {
        return visitDetailsRepository.save(theVisit);
    }

    @Override
    public void deleteById(int theId) {
        visitDetailsRepository.deleteById(theId);
    }
}
