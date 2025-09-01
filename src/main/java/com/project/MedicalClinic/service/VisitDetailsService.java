package com.project.MedicalClinic.service;



import com.project.MedicalClinic.entity.VisitDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VisitDetailsService {

    List<VisitDetails> findAll();

    Page<VisitDetails> findAll(Pageable pageable);

    boolean existsByAppointmentId(int appointmentId);

    VisitDetails findById(int theId);

    Optional<VisitDetails> findByAppointmentId(int appointmentId);

    VisitDetails save(VisitDetails theVisit);

    void deleteById(int theId);
}
