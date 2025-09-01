package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.entity.Availability;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.service.AvailabilityService;
import com.project.MedicalClinic.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/availabilities")
public class AvailabilityController {

    private AvailabilityService availabilityService;
    private DoctorService doctorService;

    public AvailabilityController(AvailabilityService availabilityService, DoctorService doctorService) {
        this.availabilityService = availabilityService;
        this.doctorService = doctorService;
    }
    @GetMapping("list")
    public String listAvailabilities(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, @RequestParam(required = false) Integer doctorId, Model theModel){

        Pageable pageable = PageRequest.of(page, size);
        Page<Availability> theAvailabilities; // = availabilityService.findAll(pageable);
        if (doctorId != null) {
            theAvailabilities = availabilityService.findByDoctorId(doctorId, pageable);
        } else {
            theAvailabilities = availabilityService.findAll(pageable);
        }

        List<Doctor> doctors = doctorService.findAll();
        theModel.addAttribute("doctors", doctors);
        theModel.addAttribute("availabilities", theAvailabilities);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", theAvailabilities.getTotalPages());
        theModel.addAttribute("selectedDoctorId", doctorId);

        return "availabilities/list-availabilities";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        //create model attribute to bind data from
        Availability theAvailability = new Availability();

        List<Doctor> doctors = doctorService.findAll();
        theModel.addAttribute("doctors", doctors);

        theModel.addAttribute("availability", theAvailability);

        return "availabilities/availability-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("availabilityId") int theId, Model theModel){

        Availability theAvailability = availabilityService.findById(theId);
        if (theAvailability == null) {
            return "redirect:/error";
        }

        // Set doctorIds for the availability
        List<Integer> doctorIds = theAvailability.getDoctors().stream()
                .map(Doctor::getId)
                .collect(Collectors.toList());
        theAvailability.setDoctorIds(doctorIds);

        List<Doctor> doctors = doctorService.findAll();
        theModel.addAttribute("doctors", doctors);
        theModel.addAttribute("availability", theAvailability);

        return "availabilities/availability-form2";
    }


    @PostMapping("/save")
    public String saveAvailability(@ModelAttribute("availability") Availability theAvailability) {
        availabilityService.saveAvailabilityForDoctors(theAvailability, theAvailability.getDoctorIds());
        return "redirect:/availabilities/list";
    }

        @GetMapping("/delete")
    public String delete(@RequestParam("availabilityId") int theid){

        availabilityService.deleteById(theid);

        return "redirect:/availabilities/list";
    }
}
