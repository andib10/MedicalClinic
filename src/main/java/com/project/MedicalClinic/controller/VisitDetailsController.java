package com.project.MedicalClinic.controller;


import com.project.MedicalClinic.entity.Appointment;
import com.project.MedicalClinic.entity.VisitDetails;
import com.project.MedicalClinic.service.AppointmentService;
import com.project.MedicalClinic.service.VisitDetailsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/details")
public class VisitDetailsController {

    private VisitDetailsService visitDetailsService;
    private AppointmentService appointmentService;

    public VisitDetailsController(VisitDetailsService visitDetailsService, AppointmentService appointmentService) {
        this.visitDetailsService = visitDetailsService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("list")
    public String listVisitDetails(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, Model theModel){

        Pageable pageable = PageRequest.of(page, size);
        Page<VisitDetails> theDetails = visitDetailsService.findAll(pageable);

        theModel.addAttribute("details", theDetails);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", theDetails.getTotalPages());

        return "details/list-details";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        VisitDetails theVisitDetail = new VisitDetails();

        theModel.addAttribute("detail", theVisitDetail);

        List<Appointment> appointments = appointmentService.findAll().stream()
                .filter(appointment -> !visitDetailsService.existsByAppointmentId(appointment.getId()))
                .collect(Collectors.toList());

        theModel.addAttribute("appointments", appointments);

        return "details/detail-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("detailId") int theId, Model theModel){

        VisitDetails theVisitDetails = visitDetailsService.findById(theId);

        theModel.addAttribute("detail", theVisitDetails);

        List<Appointment> appointments = appointmentService.findAll();
        theModel.addAttribute("appointments", appointments);

        return "details/detail-form";
    }

    @PostMapping("/save")
    public String saveVisitDetail(@ModelAttribute("detail") VisitDetails theVisitDetail) {

        visitDetailsService.save(theVisitDetail);

        return "redirect:/details/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("detailId") int theid){

        visitDetailsService.deleteById(theid);

        return "redirect:/details/list";
    }
}
