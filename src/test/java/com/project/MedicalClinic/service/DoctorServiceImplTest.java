package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.DoctorRepository;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.entity.Specialty;
import com.project.MedicalClinic.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock private DoctorRepository doctorRepository;
    @Mock private EntityManager entityManager;
    @Mock private TypedQuery<Doctor> typedQuery;
    @Mock private UserService userService;

    private DoctorServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DoctorServiceImpl(doctorRepository, entityManager, userService);
    }

    @Test
    void findAll() {
        List<Doctor> expected = List.of(new Doctor(), new Doctor());
        when(doctorRepository.findAll()).thenReturn(expected);

        List<Doctor> result = service.findAll();

        assertEquals(expected, result);
        verify(doctorRepository).findAll();
    }

    @Test
    void findAllById() {
        List<Integer> ids = List.of(1, 2, 3);
        List<Doctor> expected = List.of(new Doctor(), new Doctor(), new Doctor());
        when(doctorRepository.findAllById(ids)).thenReturn(expected);

        List<Doctor> result = service.findAllById(ids);

        assertEquals(expected, result);
        verify(doctorRepository).findAllById(ids);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Doctor> expected = new PageImpl<>(List.of(new Doctor()), pageable, 1);
        when(doctorRepository.findAll(pageable)).thenReturn(expected);

        Page<Doctor> result = service.findAll(pageable);

        assertEquals(expected, result);
        verify(doctorRepository).findAll(pageable);
    }

    @Test
    void findAllBySpecialtyId() {
        int specId = 5;
        List<Doctor> expected = List.of(new Doctor());
        when(doctorRepository.findBySpecialtyId(specId)).thenReturn(expected);

        List<Doctor> result = service.findAllBySpecialtyId(specId);

        assertEquals(expected, result);
        verify(doctorRepository).findBySpecialtyId(specId);
    }

    @Test
    void findById() {
        Doctor d = new Doctor();
        d.setId(42);
        when(doctorRepository.findById(42)).thenReturn(Optional.of(d));

        Doctor result = service.findById(42);

        assertSame(d, result);
        verify(doctorRepository).findById(42);
    }

    @Test
    void findByUsername() {
        String username = "doc.user";
        Doctor d = new Doctor();

        when(entityManager.createQuery(
                eq("SELECT d FROM Doctor d JOIN d.user u WHERE u.userName = :uName"),
                eq(Doctor.class))
        ).thenReturn(typedQuery);
        when(typedQuery.setParameter("uName", username)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(d);

        Doctor result = service.findByUsername(username);

        assertSame(d, result);
        verify(entityManager).createQuery(anyString(), eq(Doctor.class));
        verify(typedQuery).setParameter("uName", username);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void saveUpdatesExistingDoctor() {
        User existingUser = new User();
        existingUser.setUserName("doc.user");
        Doctor existing = new Doctor();
        existing.setId(10);
        existing.setUser(existingUser);

        Doctor input = new Doctor();
        input.setId(10);
        Date newHire = new Date();
        input.setHiringDate(newHire);
        Specialty newSpec = new Specialty();
        newSpec.setId(3);
        input.setSpecialty(newSpec);

        when(doctorRepository.findById(10)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(inv -> inv.getArgument(0));

        Doctor saved = service.save(input);

        assertEquals(newHire, saved.getHiringDate());
        assertEquals(newSpec, saved.getSpecialty());

        assertSame(existingUser, saved.getUser());

        verify(doctorRepository).findById(10);
        verify(doctorRepository).save(existing);
    }


    @Test
    void saveNewDoc() {
        Doctor d = new Doctor();
        when(doctorRepository.save(d)).thenReturn(d);

        Doctor saved = service.saveNewDoc(d);

        assertSame(d, saved);
        verify(doctorRepository).save(d);
    }

    @Test
    void deleteById() {
        service.deleteById(77);
        verify(doctorRepository).deleteById(77);
    }
}