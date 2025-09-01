package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.entity.*;
import com.project.MedicalClinic.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private DoctorService doctorService;
    private SpecialtyService specialtyService;
    private AvailabilityService availabilityService;
    private AppointmentService appointmentService;
    private UserService userService;
    private VisitDetailsService visitDetailsService;

    public DoctorController(DoctorService doctorService, SpecialtyService specialtyService, AvailabilityService availabilityService,
                            AppointmentService appointmentService, UserService userService, VisitDetailsService visitDetailsService) {
        this.doctorService = doctorService;
        this.specialtyService = specialtyService;
        this.availabilityService = availabilityService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.visitDetailsService = visitDetailsService;
    }

    @GetMapping("list")
    public String listDoctors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, Model theModel){

        Pageable pageable = PageRequest.of(page, size);

        Page<Doctor> theDoctors = doctorService.findAll(pageable);

        theModel.addAttribute("doctors", theDoctors);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", theDoctors.getTotalPages());

        return "doctors/list-doctors";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        Doctor theDoctor = new Doctor();
        User theUser = new User();


        List<Specialty> specialties = specialtyService.findAll();
        theModel.addAttribute("specialties", specialties);

        theModel.addAttribute("doctor", theDoctor);

        theModel.addAttribute("user", theUser);

        return "doctors/doctor-form2";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("doctorId") int theId, Model theModel){

        Doctor theDoctor = doctorService.findById(theId);

        List<Specialty> specialties = specialtyService.findAll();
        theModel.addAttribute("specialties", specialties);

        theModel.addAttribute("doctor", theDoctor);

        return "doctors/doctor-form";
    }

    @PostMapping("/save")
    public String saveDoctor(@ModelAttribute("doctor") Doctor theDoctor) {

        doctorService.save(theDoctor);

        return "redirect:/doctors/list";
    }

    @PostMapping("/saveNewDoc")
    public String saveNewDoctor(@ModelAttribute("doctor") Doctor theDoctor, @ModelAttribute("user") User theUser) {

        userService.saveUserDoctor(theUser, theDoctor);

        doctorService.saveNewDoc(theDoctor);

        return "redirect:/doctors/list";
    }


    @GetMapping("/delete")
    public String delete(@RequestParam("doctorId") int theid){

        doctorService.deleteById(theid);

        return "redirect:/doctors/list";
    }
    ///////////////////////////////////////////
    /// logged-in doctor's availabilities

    @GetMapping("/availabilities")
    public String showAvailabilities(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "6") int size,
                                     Model model, Principal principal){

        Doctor doctor = doctorService.findByUsername(principal.getName());
        Pageable pageable = PageRequest.of(page, size);
        Page<Availability> availabilitiesPage = availabilityService.findByDoctorId(doctor.getId(), pageable);

        model.addAttribute("availabilities", availabilitiesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", availabilitiesPage.getTotalPages());
        return "doctors/availabilities";
    }

    @GetMapping("/showFormForAddAvailability")
    public String showFormForAddAvailability(Model theModel){

        Availability theAvailability = new Availability();

        theModel.addAttribute("availability", theAvailability);

        return "doctors/availability-doc-form";
    }

    @PostMapping("/saveAvailability")
    public String saveAvailability(@ModelAttribute("availability") Availability theAvailability, Principal principal) {
        Doctor doctor = doctorService.findByUsername(principal.getName()); // Fetch the doctor based on logged-in user
        // Check if the time slot already exists
        Optional<Availability> existingAvailabilityOpt = availabilityService.findByTimeSlot(theAvailability.getTimeSlot());
        Availability availability;
        if (existingAvailabilityOpt.isPresent()) {
            availability = existingAvailabilityOpt.get();
        } else {
            availability = theAvailability;
        }

        // Ensure the doctor is linked to the availability
        if (availability.getDoctors() == null) {
            availability.setDoctors(new ArrayList<>());
        }
        if (!availability.getDoctors().contains(doctor)) {
            availability.getDoctors().add(doctor);
        }

        availabilityService.save(availability);

        return "redirect:/doctors/availabilities";
    }

    @GetMapping("/deleteAvailability")
    public String deleteAvailability(@RequestParam("availabilityId") int theId, Principal principal){

        Doctor doctor = doctorService.findByUsername(principal.getName());
        Availability availability = availabilityService.findById(theId);

        if (availability == null || !availability.getDoctors().contains(doctor)) {
            return "redirect:/access-denied";
        }

        if (availability != null) {
            availability.getDoctors().remove(doctor);
            if (availability.getDoctors().isEmpty()) {
                // if there are no more doctors associated with this availability, delete it
                availabilityService.deleteById(theId);
            } else {
                // otherwise, just save the updated availability
                availabilityService.save(availability);
            }
        }

        return "redirect:/doctors/availabilities";
    }
    //////////////////////////////////////////////////////////////
    /// logged-in doctor's appointments

    @GetMapping("/appointments")
    public String showAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) Integer patientId,
            Model model,
            Principal principal
    ){

        Doctor doctor = doctorService.findByUsername(principal.getName());
        Pageable pageable = PageRequest.of(page, size);

        Page<Appointment> appointmentsPage;
        if(patientId != null) {
            appointmentsPage = appointmentService.findByDoctorIdAndPatientId(doctor.getId(), patientId, pageable);
        } else {
            appointmentsPage = appointmentService.findAllByDoctorId(doctor.getId(), pageable);
        }

        List<Appointment> allAppointments = appointmentService.findAllByDoctorId(doctor.getId());

        List<Patient> doctorPatients = allAppointments.stream()
                .filter(appointment -> appointment.getPatient() != null)
                        .map(Appointment::getPatient)
                                .distinct()
                                        .collect(Collectors.toList());

        model.addAttribute("appointments", appointmentsPage.getContent());
        model.addAttribute("patients", doctorPatients);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());
        model.addAttribute("selectedPatientId", patientId);

        return "doctors/appointments";
    }



    @GetMapping("/viewAppointmentDetails/{appointmentId}")
    public String viewAppointmentDetails(@PathVariable int appointmentId, Model model, Principal principal) {

        Appointment appointment = appointmentService.findById(appointmentId);
        Doctor doctor = doctorService.findByUsername(principal.getName());

        if (appointment == null || appointment.getDoctor().getId() != doctor.getId()) {
            return "redirect:/access-denied";
        }

        Optional<VisitDetails> visitDetailsOptional = visitDetailsService.findByAppointmentId(appointmentId);
        model.addAttribute("visitDetails", visitDetailsOptional.orElse(new VisitDetails()));
        model.addAttribute("appointment", appointmentService.findById(appointmentId));
        return "doctors/view-appointment-details";
    }



    @PostMapping("/saveDiagnosis")
    public String saveDiagnosis(@ModelAttribute("visitDetails") VisitDetails visitDetails,
                                @RequestParam("appointmentId") int appointmentId,
                                RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.findById(appointmentId);
            if (appointment != null) {
                visitDetails.setAppointment(appointment);
                visitDetailsService.save(visitDetails);
                //redirectAttributes.addFlashAttribute("success", "Diagnosis and visit details saved successfully.");
                return "redirect:/doctors/viewAppointmentDetails/" + appointment.getId();
            } else {
                //redirectAttributes.addFlashAttribute("error", "Failed to find the appointment.");
                return "redirect:/doctors/appointments";
            }
        } catch (Exception e) {
            //redirectAttributes.addFlashAttribute("error", "Error saving the diagnosis: " + e.getMessage());
            return "redirect:/doctors/appointments";
        }
    }



    @GetMapping("/addOrViewDiagnosis/{appointmentId}")
    public String addOrViewDiagnosis(@PathVariable int appointmentId, RedirectAttributes redirectAttributes, Model model, Principal principal) {

        Appointment appointment = appointmentService.findById(appointmentId);
        Doctor doctor = doctorService.findByUsername(principal.getName());

        if (appointment == null || appointment.getDoctor().getId() != doctor.getId()) {
            return "redirect:/access-denied";
        }

        Optional<VisitDetails> visitDetailsOptional = visitDetailsService.findByAppointmentId(appointmentId);
        if (visitDetailsOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("message", "Detaliile programării au fost deja adăugate.");
            return "redirect:/doctors/viewAppointmentDetails/" + appointmentId;
        } else {
            model.addAttribute("visitDetails", new VisitDetails());
            model.addAttribute("appointmentId", appointmentId);
            return "doctors/add-diagnosis";
        }
    }


    @GetMapping("/deleteAppointment")
    public String deleteAppointment(@RequestParam("appointmentId") int theid, Principal principal){

        Appointment appointment = appointmentService.findById(theid);
        Doctor doctor = doctorService.findByUsername(principal.getName());

        if (appointment == null || appointment.getDoctor().getId() != doctor.getId()) {
            return "redirect:/access-denied";
        }

        appointmentService.deleteById(theid);
        return "redirect:/doctors/appointments";
    }
}
