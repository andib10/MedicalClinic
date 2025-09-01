package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.PatientRepository;
import com.project.MedicalClinic.entity.Patient;
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
class PatientServiceImplTest {

    @Mock private PatientRepository patientRepository;
    @Mock private EntityManager entityManager;
    @Mock private TypedQuery<Patient> typedQuery;

    private PatientServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PatientServiceImpl(patientRepository, entityManager);
    }

    @Test
    void findAll() {
        List<Patient> expected = List.of(new Patient(), new Patient());
        when(patientRepository.findAll()).thenReturn(expected);

        List<Patient> result = service.findAll();

        assertEquals(expected, result);
        verify(patientRepository).findAll();
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<Patient> expected = new PageImpl<>(List.of(new Patient()), pageable, 6);
        when(patientRepository.findAll(pageable)).thenReturn(expected);

        Page<Patient> result = service.findAll(pageable);

        assertEquals(expected, result);
        verify(patientRepository).findAll(pageable);
    }

    @Test
    void findById() {
        Patient p = new Patient();
        p.setId(42);
        when(patientRepository.findById(42)).thenReturn(Optional.of(p));

        Patient result = service.findById(42);

        assertSame(p, result);
        verify(patientRepository).findById(42);
    }

    @Test
    void findByUsername() {
        String username = "john.doe";
        Patient p = new Patient();

        when(entityManager.createQuery(
                eq("SELECT p FROM Patient p JOIN p.user u WHERE u.userName = :uName"),
                eq(Patient.class))
        ).thenReturn(typedQuery);
        when(typedQuery.setParameter("uName", username)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(p);

        Patient result = service.findByUsername(username);

        assertSame(p, result);
        verify(entityManager).createQuery(anyString(), eq(Patient.class));
        verify(typedQuery).setParameter("uName", username);
        verify(typedQuery).getSingleResult();
    }

    @Test
    void save() {
        User existingUser = new User();
        existingUser.setUserName("john.doe");

        Patient existing = new Patient();
        existing.setId(10);
        existing.setUser(existingUser);

        Patient input = new Patient();
        input.setId(10);
        Date newBirth = new Date();
        input.setBirthDate(newBirth);
        input.setCNP("1234567890123");
        input.setPhoneNumber("0712345678");

        when(patientRepository.findById(10)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        Patient saved = service.save(input);

        assertEquals(newBirth, saved.getBirthDate());
        assertEquals("1234567890123", saved.getCNP());
        assertEquals("0712345678", saved.getPhoneNumber());
        assertSame(existingUser, saved.getUser());

        verify(patientRepository).findById(10);
        verify(patientRepository).save(existing);
    }

    @Test
    void deleteById() {
        service.deleteById(77);
        verify(patientRepository).deleteById(77);
    }
}