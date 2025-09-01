package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.dto.DoctorDTO;
import com.project.MedicalClinic.entity.*;
import com.project.MedicalClinic.service.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patients")
public class PatientController {
    private PatientService patientService;
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private SpecialtyService specialtyService;
    private AvailabilityService availabilityService;
    private VisitDetailsService visitDetailsService;

    public PatientController(PatientService patientService, AppointmentService appointmentService,
                             DoctorService doctorService, SpecialtyService specialtyService,
                             AvailabilityService availabilityService, VisitDetailsService visitDetailsService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.specialtyService = specialtyService;
        this.availabilityService = availabilityService;
        this.visitDetailsService = visitDetailsService;
    }

    @GetMapping("list")
    public String listPatients(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, Model theModel){

        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> thePatients = patientService.findAll(pageable);

        theModel.addAttribute("patients", thePatients);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", thePatients.getTotalPages());

        return "patients/list-patients";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        Patient thePatient = new Patient();

        theModel.addAttribute("patient", thePatient);

        return "patients/patient-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("patientId") int theId, Model theModel){

        Patient thePatient = patientService.findById(theId);

        theModel.addAttribute("patient", thePatient);

        return "patients/patient-form";
    }

    @PostMapping("/save")
    public String savePatient(@ModelAttribute("patient") Patient thePatient) {

        patientService.save(thePatient);

        return "redirect:/patients/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("patientId") int theid){

        patientService.deleteById(theid);

        return "redirect:/patients/list";
    }

    /// logged-in patient's appointments

    @GetMapping("/appointments")
    public String showAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Integer specialtyId,
            @RequestParam(required = false) Integer doctorId,
            Model model,
            Principal principal
    ) {
        Patient patient = patientService.findByUsername(principal.getName());
        Pageable pageable = PageRequest.of(page, size);

        Page<Appointment> appointmentsPage;
        if (specialtyId != null && doctorId != null) {
            appointmentsPage = appointmentService.findByPatientIdAndDoctorSpecialtyIdAndDoctorId(patient.getId(), doctorId, specialtyId, pageable);
        } else if (specialtyId != null) {
            appointmentsPage = appointmentService.findByPatientIdAndDoctorSpecialtyId(patient.getId(), specialtyId, pageable);
        } else if (doctorId != null) {
            appointmentsPage = appointmentService.findByPatientIdAndDoctorId(patient.getId(), doctorId, pageable);
        } else {
            appointmentsPage = appointmentService.findAllByPatientId(patient.getId(), pageable);
        }

        List<Appointment> allAppointments = appointmentService.findAllByPatientId(patient.getId());

        List<Specialty> patientSpecialties = allAppointments.stream()
                .filter(appointment -> appointment.getDoctor() != null)
                .map(appointment -> appointment.getDoctor().getSpecialty())
                .distinct()
                .collect(Collectors.toList());

        List<Doctor> patientDoctors = allAppointments.stream()
                .filter(appointment -> appointment.getDoctor() != null)
                .map(Appointment::getDoctor)
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointmentsPage.getContent());
        model.addAttribute("specialties", patientSpecialties);
        model.addAttribute("doctors", patientDoctors);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());
        model.addAttribute("selectedSpecialtyId", specialtyId);
        model.addAttribute("selectedDoctorId", doctorId);

        return "patients/appointments";
    }


    @GetMapping("/deleteAppointment")
    public String deleteAppointment(@RequestParam("appointmentId") int theid, Principal principal){

        Appointment appointment = appointmentService.findById(theid);
        Patient patientt = patientService.findByUsername(principal.getName());

        if (appointment == null || appointment.getPatient().getId() != patientt.getId()) {
            return "redirect:/access-denied";
        }

        appointmentService.deleteById(theid);
        return "redirect:/patients/appointments";
    }

    /// booking appointment process
    @GetMapping("/book")
    public String loadBookingPage(Model model) {
        model.addAttribute("specialties", specialtyService.findAll());
        return "patients/book-appointment";
    }


/*    retrieves a list of doctors by specialty from the database and transforms them into DoctorDTO objects.
    It then returns these DTOs in a ResponseEntity, ensuring efficient data transfer
    and encapsulation by only exposing necessary information.*/


    @Operation(
            summary = "Listeaza doctorii dupa specialitate",
            description = "Returneaza o listă de doctori pentru specialitatea dată."
    )
    @ResponseBody
    @GetMapping("/doctors/{specialtyId}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialty(@PathVariable("specialtyId") int specialtyId) {
        List<Doctor> doctors = doctorService.findAllBySpecialtyId(specialtyId);
        List<DoctorDTO> doctorDTOs = doctors.stream()
                .map(doctor -> new DoctorDTO(doctor.getId(),
                        doctor.getUser().getFirstName(),
                        doctor.getUser().getLastName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorDTOs);
    }

    @Operation(
            summary = "Intervale disponibile ale unui doctor",
            description = "Returneaza toate sloturile orare libere pentru doctorul specificat."
    )
    @ResponseBody
    @GetMapping("/availabilities/{doctorId}")
    public List<Availability> getAvailabilitiesByDoctor(@PathVariable("doctorId") int doctorId) {
        return availabilityService.findAvailableTimeSlots(doctorId);
    }

    @PostMapping("/bookAppointment")
    public String bookAppointment(@RequestParam("doctorId") int doctorId, @RequestParam("timeSlot") String slotStart, Principal principal) {
        Appointment appointment = new Appointment();

        Doctor doc = doctorService.findById(doctorId);
        Patient patient = patientService.findByUsername(principal.getName());

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setSlotStart(LocalDateTime.parse(slotStart));
        appointmentService.save(appointment);

        return "redirect:/patients/appointments";
    }

    // view appointment details
    @GetMapping("/viewAppointmentDetails/{appointmentId}")
    public String viewAppointmentDetails(@PathVariable int appointmentId, Model model, Principal principal) {

        Appointment appointment = appointmentService.findById(appointmentId);
        Patient patient = patientService.findByUsername(principal.getName());

        if (appointment == null || appointment.getPatient().getId() != patient.getId()) {
            return "redirect:/access-denied";
        }

        Optional<VisitDetails> visitDetailsOptional = visitDetailsService.findByAppointmentId(appointmentId);
        model.addAttribute("visitDetails", visitDetailsOptional.orElse(new VisitDetails()));
        model.addAttribute("appointment", appointmentService.findById(appointmentId));
        return "patients/view-appointment-details";
    }

}
