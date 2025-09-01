package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.entity.Availability;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.entity.User;
import com.project.MedicalClinic.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private PatientService patientService;
    @MockBean private AppointmentService appointmentService;
    @MockBean private DoctorService doctorService;
    @MockBean private SpecialtyService specialtyService;
    @MockBean private AvailabilityService availabilityService;
    @MockBean private VisitDetailsService visitDetailsService;

    @Test
    void getDoctorsBySpecialty() throws Exception {
        int specialtyId = 1;

        User user = new User();
        user.setFirstName("Ana");
        user.setLastName("Ionescu");

        Doctor doctor = new Doctor();
        doctor.setId(11);
        doctor.setUser(user);

        when(doctorService.findAllBySpecialtyId(specialtyId))
                .thenReturn(Collections.singletonList(doctor));

        mockMvc.perform(get("/patients/doctors/{specialtyId}", specialtyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(11))
                .andExpect(jsonPath("$[0].firstName").value("Ana"))
                .andExpect(jsonPath("$[0].lastName").value("Ionescu"));
    }

    @Test
    void getAvailabilitiesByDoctor() throws Exception {
        int doctorId = 2;

        Availability availability = new Availability();
        availability.setId(22);
        availability.setTimeSlot(LocalDateTime.of(2025, 9, 1, 10, 0));

        when(availabilityService.findAvailableTimeSlots(doctorId))
                .thenReturn(List.of(availability));

        mockMvc.perform(get("/patients/availabilities/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(22))
                .andExpect(jsonPath("$[0].timeSlot").value("2025-09-01T10:00:00"));
    }
}