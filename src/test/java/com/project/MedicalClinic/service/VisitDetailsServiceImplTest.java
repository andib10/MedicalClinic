package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.VisitDetailsRepository;
import com.project.MedicalClinic.entity.VisitDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitDetailsServiceImplTest {

    @Mock
    private VisitDetailsRepository visitDetailsRepository;

    private VisitDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VisitDetailsServiceImpl(visitDetailsRepository);
    }

    @Test
    void findAll() {
        List<VisitDetails> expected = List.of(new VisitDetails(), new VisitDetails());
        when(visitDetailsRepository.findAll()).thenReturn(expected);

        List<VisitDetails> result = service.findAll();

        assertEquals(expected, result);
        verify(visitDetailsRepository).findAll();
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<VisitDetails> expected = new PageImpl<>(List.of(new VisitDetails(), new VisitDetails()));
        when(visitDetailsRepository.findAll(pageable)).thenReturn(expected);

        Page<VisitDetails> result = service.findAll(pageable);

        assertEquals(expected, result);
        verify(visitDetailsRepository).findAll(pageable);
    }

    @Test
    void existsByAppointmentId() {
        when(visitDetailsRepository.existsByAppointmentId(1)).thenReturn(true);

        assertTrue(service.existsByAppointmentId(1));
        verify(visitDetailsRepository).existsByAppointmentId(1);
    }

    @Test
    void findById() {
        VisitDetails vd = new VisitDetails();
        vd.setId(5);
        when(visitDetailsRepository.findById(5)).thenReturn(Optional.of(vd));

        VisitDetails result = service.findById(5);

        assertSame(vd, result);
        verify(visitDetailsRepository).findById(5);
    }

    @Test
    void findByAppointmentId() {
        VisitDetails vd = new VisitDetails();
        when(visitDetailsRepository.findByAppointmentId(10)).thenReturn(Optional.of(vd));

        Optional<VisitDetails> result = service.findByAppointmentId(10);

        assertTrue(result.isPresent());
        assertSame(vd, result.get());
        verify(visitDetailsRepository).findByAppointmentId(10);
    }

    @Test
    void save() {
        VisitDetails vd = new VisitDetails();
        when(visitDetailsRepository.save(vd)).thenReturn(vd);

        VisitDetails result = service.save(vd);

        assertSame(vd, result);
        verify(visitDetailsRepository).save(vd);
    }

    @Test
    void deleteById() {
        service.deleteById(15);
        verify(visitDetailsRepository).deleteById(15);
    }
}