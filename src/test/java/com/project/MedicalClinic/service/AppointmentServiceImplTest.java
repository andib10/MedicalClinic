package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.AppointmentRepository;
import com.project.MedicalClinic.entity.Appointment;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AppointmentServiceImpl(appointmentRepository);
    }

    @Test
    void findAll() {
        List<Appointment> expected = List.of(new Appointment(), new Appointment());
        when(appointmentRepository.findAll()).thenReturn(expected);

        List<Appointment> result = service.findAll();

        assertEquals(expected, result);
        verify(appointmentRepository).findAll();
    }

    @Test
    void testFindAllPageableSort() {
        // given input pageable (no sort)
        Pageable in = PageRequest.of(2, 5);
        List<Appointment> content = List.of(new Appointment());
        // capture pageable passed to repository
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        // stub repository to return a page for any pageable
        when(appointmentRepository.findAll(any(Pageable.class)))
                .thenAnswer(inv -> {
                    Pageable p = inv.getArgument(0);
                    return new PageImpl<>(content, p, 1);
                });

        // when
        Page<Appointment> page = service.findAll(in);

        // then: repository called with a pageable that has same page/size, DESC by slotStart
        verify(appointmentRepository).findAll(captor.capture());
        Pageable used = captor.getValue();

        assertEquals(2, used.getPageNumber());
        assertEquals(5, used.getPageSize());

        Sort sort = used.getSort();
        assertTrue(sort.isSorted(), "Sort should be applied");
        Sort.Order order = sort.getOrderFor("slotStart");
        assertNotNull(order, "Sort should contain slotStart");
        assertEquals(Sort.Direction.DESC, order.getDirection(), "slotStart should be DESC");

        assertEquals(content, page.getContent());
    }

    @Test
    void findAllByDoctorId() {
        int doctorId = 10;
        List<Appointment> expected = List.of(new Appointment(), new Appointment());
        when(appointmentRepository.findByDoctorId(doctorId)).thenReturn(expected);

        List<Appointment> result = service.findAllByDoctorId(doctorId);

        assertEquals(expected, result);
        verify(appointmentRepository).findByDoctorId(doctorId);
    }

    @Test
    void testFindAllByDoctorIdPageable() {
        int doctorId = 10;
        Pageable pageable = PageRequest.of(0, 3);
        Page<Appointment> expected = new PageImpl<>(List.of(new Appointment()), pageable, 1);

        when(appointmentRepository.findByDoctorIdOrderBySlotStartDesc(doctorId, pageable))
                .thenReturn(expected);

        Page<Appointment> result = service.findAllByDoctorId(doctorId, pageable);

        assertEquals(expected, result);
        verify(appointmentRepository).findByDoctorIdOrderBySlotStartDesc(doctorId, pageable);
    }

    @Test
    void findByDoctorIdAndPatientId() {
        int doctorId = 10, patientId = 20;
        Pageable pageable = PageRequest.of(1, 4);
        Page<Appointment> expected = new PageImpl<>(List.of(new Appointment()), pageable, 5);

        when(appointmentRepository.findByDoctorIdAndPatientId(doctorId, patientId, pageable))
                .thenReturn(expected);

        Page<Appointment> result = service.findByDoctorIdAndPatientId(doctorId, patientId, pageable);

        assertEquals(expected, result);
        verify(appointmentRepository).findByDoctorIdAndPatientId(doctorId, patientId, pageable);
    }

    @Test
    void findAllByPatientIdReturnsList() {
        int patientId = 30;
        List<Appointment> expected = List.of(new Appointment());
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(expected);

        List<Appointment> result = service.findAllByPatientId(patientId);

        assertEquals(expected, result);
        verify(appointmentRepository).findByPatientId(patientId);
    }

    @Test
    void testFindAllByPatientIdPageableOrdered() {
        int patientId = 30;
        Pageable pageable = PageRequest.of(0, 2);
        Page<Appointment> expected = new PageImpl<>(List.of(new Appointment()), pageable, 2);

        when(appointmentRepository.findByPatientIdOrderBySlotStartDesc(patientId, pageable))
                .thenReturn(expected);

        Page<Appointment> result = service.findAllByPatientId(patientId, pageable);

        assertEquals(expected, result);
        verify(appointmentRepository).findByPatientIdOrderBySlotStartDesc(patientId, pageable);
    }

    @Test
    void findByPatientIdAndDoctorSpecialtyId() {
        int patientId = 1, specialtyId = 2;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Appointment> expected = new PageImpl<>(List.of(new Appointment()), pageable, 1);

        when(appointmentRepository.findByPatientIdAndDoctorSpecialtyId(patientId, specialtyId, pageable))
                .thenReturn(expected);

        Page<Appointment> result =
                service.findByPatientIdAndDoctorSpecialtyId(patientId, specialtyId, pageable);

        assertEquals(expected, result);
        verify(appointmentRepository).findByPatientIdAndDoctorSpecialtyId(patientId, specialtyId, pageable);
    }

    @Test
    void findByPatientIdAndDoctorId() {
        int patientId = 1, doctorId = 9;
        Pageable pageable = PageRequest.of(2, 2);
        Page<Appointment> expected = new PageImpl<>(List.of(new Appointment()), pageable, 4);

        when(appointmentRepository.findByPatientIdAndDoctorId(patientId, doctorId, pageable))
                .thenReturn(expected);

        Page<Appointment> result =
                service.findByPatientIdAndDoctorId(patientId, doctorId, pageable);

        assertEquals(expected, result);
        verify(appointmentRepository).findByPatientIdAndDoctorId(patientId, doctorId, pageable);
    }

    @Test
    void findByPatientIdAndDoctorSpecialtyIdAndDoctorId() {
        int patientId = 1, specialtyId = 2, doctorId = 3;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Appointment> expected = new PageImpl<>(List.of(new Appointment()), pageable, 1);

        when(appointmentRepository.findByPatientIdAndDoctorSpecialtyIdAndDoctorId(patientId, specialtyId, doctorId, pageable))
                .thenReturn(expected);

        Page<Appointment> result =
                service.findByPatientIdAndDoctorSpecialtyIdAndDoctorId(patientId, specialtyId, doctorId, pageable);

        assertEquals(expected, result);
        verify(appointmentRepository).findByPatientIdAndDoctorSpecialtyIdAndDoctorId(patientId, specialtyId, doctorId, pageable);
    }

    @Test
    void findById() {
        Appointment a = new Appointment();
        a.setId(42);
        when(appointmentRepository.findById(42)).thenReturn(Optional.of(a));

        Appointment result = service.findById(42);

        assertSame(a, result);
        verify(appointmentRepository).findById(42);
    }

    @Test
    void findByIdNotFound() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99));
        assertTrue(ex.getMessage().contains("Did not find appointment id - 99"));
        verify(appointmentRepository).findById(99);
    }

    @Test
    void save() {
        Appointment a = new Appointment();
        a.setSlotStart(LocalDateTime.now());

        when(appointmentRepository.save(a)).thenReturn(a);

        Appointment result = service.save(a);

        assertSame(a, result);
        verify(appointmentRepository).save(a);
    }

    @Test
    void deleteById() {
        service.deleteById(77);
        verify(appointmentRepository).deleteById(77);
    }
}