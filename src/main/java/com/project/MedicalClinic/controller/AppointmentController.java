package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.entity.Appointment;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.entity.Patient;
import com.project.MedicalClinic.service.AppointmentService;
import com.project.MedicalClinic.service.DoctorService;
import com.project.MedicalClinic.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    
    private AppointmentService appointmentService;
    private DoctorService doctorService;
    private PatientService patientService;

    public AppointmentController(AppointmentService appointmentService, PatientService patientService, DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }


    @GetMapping("list")
    public String listAppointments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, Model theModel){
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> theAppointments = appointmentService.findAll(pageable);


        theModel.addAttribute("appointments", theAppointments);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", theAppointments.getTotalPages());

        return "appointments/list-appointments";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        //create model attribute to bind data from
        Appointment theAppointment = new Appointment();


        List<Patient> patients = patientService.findAll();
        theModel.addAttribute("patients", patients);

        List<Doctor> doctors = doctorService.findAll();
        theModel.addAttribute("doctors", doctors);

        theModel.addAttribute("appointment", theAppointment);

        return "appointments/appointment-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("appointmentId") int theId, Model theModel){

        Appointment theAppointment = appointmentService.findById(theId);

        List<Patient> patients = patientService.findAll();
        theModel.addAttribute("patients", patients);

        List<Doctor> doctors = doctorService.findAll();
        theModel.addAttribute("doctors", doctors);

        // set appointment as a model attribute to pre-populate the form
        theModel.addAttribute("appointment", theAppointment);

        return "appointments/appointment-form";
    }

    @PostMapping("/save")
    public String saveAppointment(@ModelAttribute("appointment") Appointment theAppointment) {

        appointmentService.save(theAppointment);

        return "redirect:/appointments/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("appointmentId") int theid){

        appointmentService.deleteById(theid);

        return "redirect:/appointments/list";
    }
}
