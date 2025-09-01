package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.AppointmentRepository;
import com.project.MedicalClinic.dao.AvailabilityRepository;
import com.project.MedicalClinic.dao.DoctorRepository;
import com.project.MedicalClinic.entity.Appointment;
import com.project.MedicalClinic.entity.Availability;
import com.project.MedicalClinic.entity.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailabilityServiceImpl implements AvailabilityService{

    private AvailabilityRepository availabilityRepository;
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository, AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    @Override
    public Page<Availability> findAll(Pageable pageable) {
        return availabilityRepository.findAll(pageable);
    }


    @Override
    public Page<Availability> findByDoctorId(int doctorId, Pageable pageable) {
        return availabilityRepository.findByDoctorsIdOrderByTimeSlotDesc(doctorId, pageable);
    }

    @Override
    public Optional<Availability> findByTimeSlot(LocalDateTime timeSlot) {
        return availabilityRepository.findByTimeSlot(timeSlot);
    }

    // returns only available time slots for a given doctor.
    // exclude slots that already have appointments, unless those appointments have been canceled.
    @Override
    public List<Availability> findAvailableTimeSlots(int doctorId) {
        // fetch all available time slots for the doctor
        List<Availability> allSlots = availabilityRepository.findByDoctorsId(doctorId);
        // fetch all booked appointments for the doctor
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorId(doctorId);

        // extract the booked slot start times
        Set<LocalDateTime> bookedSlotStarts = bookedAppointments.stream()
                .map(Appointment::getSlotStart)
                .collect(Collectors.toSet());

        // filter out the booked slots and past slots
        LocalDateTime now = LocalDateTime.now();
        return allSlots.stream()
                .filter(slot -> !bookedSlotStarts.contains(slot.getTimeSlot()) && slot.getTimeSlot().isAfter(now))
                .collect(Collectors.toList());
    }

    @Override
    public Availability findById(int theId) {
        Optional<Availability> result = availabilityRepository.findById(theId);

        Availability theAvailability = null;

        if (result.isPresent()) {
            theAvailability = result.get();
        }
        else {
            throw new RuntimeException("Did not find availability id - " + theId);
        }

        return theAvailability;
    }

    @Override
    public Availability save(Availability theAvailability) {
        return availabilityRepository.save(theAvailability);
    }

    @Override
    public void saveAvailabilityForDoctors(Availability availability, List<Integer> doctorIds) {
        List<Doctor> doctors = doctorRepository.findAllById(doctorIds);

        Optional<Availability> existingAvailabilityOpt = availabilityRepository.findByTimeSlot(availability.getTimeSlot());
        Availability finalAvailability;

        if (existingAvailabilityOpt.isPresent()) {
            finalAvailability = existingAvailabilityOpt.get();
        } else {
            finalAvailability = availability;
        }
        if (finalAvailability.getDoctors() == null) {
            finalAvailability.setDoctors(new ArrayList<>());  // Ensure the list is initialized
        }

        for (Doctor doctor : doctors) {
            if (!finalAvailability.getDoctors().contains(doctor)) {
                finalAvailability.getDoctors().add(doctor);
            }
        }

        availabilityRepository.save(finalAvailability);
    }


    @Override
    public void deleteById(int theId) {
        availabilityRepository.deleteById(theId);
    }
}
