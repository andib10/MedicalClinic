package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.SpecialtyRepository;
import com.project.MedicalClinic.entity.Specialty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    private SpecialtyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SpecialtyServiceImpl(specialtyRepository);
    }

    @Test
    void findAll() {
        List<Specialty> expected = List.of(new Specialty(), new Specialty());
        when(specialtyRepository.findAll()).thenReturn(expected);

        List<Specialty> result = service.findAll();

        assertEquals(expected, result);
        verify(specialtyRepository).findAll();
    }

    @Test
    void findById() {
        Specialty s = new Specialty();
        s.setId(10);
        when(specialtyRepository.findById(10)).thenReturn(Optional.of(s));

        Specialty result = service.findById(10);

        assertSame(s, result);
        verify(specialtyRepository).findById(10);
    }

    @Test
    void findByIdNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(99));
        assertTrue(ex.getMessage().contains("Did not find specialty id - 99"));
        verify(specialtyRepository).findById(99);
    }

    @Test
    void save() {
        Specialty s = new Specialty();
        s.setName("Cardiologie");
        when(specialtyRepository.save(s)).thenReturn(s);

        Specialty result = service.save(s);

        assertSame(s, result);
        verify(specialtyRepository).save(s);
    }

    @Test
    void deleteById() {
        service.deleteById(7);
        verify(specialtyRepository).deleteById(7);
    }
}