package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.AppointmentRepository;
import com.project.MedicalClinic.dao.AvailabilityRepository;
import com.project.MedicalClinic.dao.DoctorRepository;
import com.project.MedicalClinic.entity.Appointment;
import com.project.MedicalClinic.entity.Availability;
import com.project.MedicalClinic.entity.Doctor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock private AvailabilityRepository availabilityRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;

    private AvailabilityServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AvailabilityServiceImpl(availabilityRepository, appointmentRepository, doctorRepository);
    }

    @Test
    void findAll() {
        List<Availability> expected = List.of(new Availability(), new Availability());
        when(availabilityRepository.findAll()).thenReturn(expected);

        List<Availability> result = service.findAll();

        assertEquals(expected, result);
        verify(availabilityRepository).findAll();
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(1, 10);
        Page<Availability> expected = new PageImpl<>(List.of(new Availability()), pageable, 11);
        when(availabilityRepository.findAll(pageable)).thenReturn(expected);

        Page<Availability> result = service.findAll(pageable);

        assertEquals(expected, result);
        verify(availabilityRepository).findAll(pageable);
    }

    @Test
    void findByDoctorId() {
        int doctorId = 5;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Availability> expected = new PageImpl<>(List.of(new Availability()), pageable, 1);
        when(availabilityRepository.findByDoctorsIdOrderByTimeSlotDesc(doctorId, pageable))
                .thenReturn(expected);

        Page<Availability> result = service.findByDoctorId(doctorId, pageable);

        assertEquals(expected, result);
        verify(availabilityRepository).findByDoctorsIdOrderByTimeSlotDesc(doctorId, pageable);
    }

    @Test
    void findByTimeSlot() {
        LocalDateTime ts = LocalDateTime.now().plusDays(1);
        Availability a = new Availability();
        a.setId(10);
        when(availabilityRepository.findByTimeSlot(ts)).thenReturn(Optional.of(a));

        Optional<Availability> result = service.findByTimeSlot(ts);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getId());
        verify(availabilityRepository).findByTimeSlot(ts);
    }

    @Test
    void findAvailableTimeSlots() {
        int doctorId = 7;
        LocalDateTime now = LocalDateTime.now();

        // available slots
        Availability past = new Availability();
        past.setId(1);
        past.setTimeSlot(now.minusHours(1));

        Availability freeFuture = new Availability();
        freeFuture.setId(2);
        freeFuture.setTimeSlot(now.plusHours(2));

        Availability bookedFuture = new Availability();
        bookedFuture.setId(3);
        bookedFuture.setTimeSlot(now.plusHours(3));

        when(availabilityRepository.findByDoctorsId(doctorId))
                .thenReturn(List.of(past, freeFuture, bookedFuture));

        // appointments (booked) for doctor
        Appointment apBooked = new Appointment();
        apBooked.setSlotStart(bookedFuture.getTimeSlot()); // same timeslot => exclus

        when(appointmentRepository.findByDoctorId(doctorId))
                .thenReturn(List.of(apBooked));

        List<Availability> result = service.findAvailableTimeSlots(doctorId);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
        verify(availabilityRepository).findByDoctorsId(doctorId);
        verify(appointmentRepository).findByDoctorId(doctorId);
    }


    @Test
    void findById() {
        Availability a = new Availability();
        a.setId(42);
        when(availabilityRepository.findById(42)).thenReturn(Optional.of(a));

        Availability result = service.findById(42);

        assertSame(a, result);
        verify(availabilityRepository).findById(42);
    }

    @Test
    void save() {
        Availability a = new Availability();
        when(availabilityRepository.save(a)).thenReturn(a);

        Availability result = service.save(a);

        assertSame(a, result);
        verify(availabilityRepository).save(a);
    }

    @Test
    void saveAvailabilityForDoctors() {
        // arrange
        LocalDateTime ts = LocalDateTime.now().plusDays(1);
        Availability input = new Availability();
        input.setTimeSlot(ts);

        Doctor d1 = new Doctor(); d1.setId(1);
        Doctor d2 = new Doctor(); d2.setId(2);
        List<Integer> doctorIds = List.of(1, 2);

        when(doctorRepository.findAllById(doctorIds)).thenReturn(List.of(d1, d2));
        when(availabilityRepository.findByTimeSlot(ts)).thenReturn(Optional.empty());

        ArgumentCaptor<Availability> captor = ArgumentCaptor.forClass(Availability.class);


        service.saveAvailabilityForDoctors(input, doctorIds);


        verify(availabilityRepository).save(captor.capture());
        Availability saved = captor.getValue();

        assertEquals(ts, saved.getTimeSlot());
        assertNotNull(saved.getDoctors());

        assertTrue(saved.getDoctors().contains(d1));
        assertTrue(saved.getDoctors().contains(d2));
    }

    @Test
    void saveAvailabilityForDoctors_usesExistingAvailability_andAvoidsDuplicates() {

        LocalDateTime ts = LocalDateTime.now().plusDays(1);

        Doctor d1 = new Doctor(); d1.setId(1);
        Doctor d2 = new Doctor(); d2.setId(2);

        Availability existing = new Availability();
        existing.setId(100);
        existing.setTimeSlot(ts);
        existing.setDoctors(new ArrayList<>(List.of(d1)));

        when(availabilityRepository.findByTimeSlot(ts)).thenReturn(Optional.of(existing));
        when(doctorRepository.findAllById(List.of(1, 2))).thenReturn(List.of(d1, d2));

        ArgumentCaptor<Availability> captor = ArgumentCaptor.forClass(Availability.class);

        Availability input = new Availability();
        input.setTimeSlot(ts);

        service.saveAvailabilityForDoctors(input, List.of(1, 2));

        verify(availabilityRepository).save(captor.capture());
        Availability saved = captor.getValue();

        assertEquals(100, saved.getId());

        assertEquals(2, saved.getDoctors().size());
        assertTrue(saved.getDoctors().contains(d1));
        assertTrue(saved.getDoctors().contains(d2));
    }

    @Test
    void deleteById() {
        service.deleteById(77);
        verify(availabilityRepository).deleteById(77);
    }
}