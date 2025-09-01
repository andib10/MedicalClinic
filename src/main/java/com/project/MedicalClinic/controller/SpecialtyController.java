package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.entity.Specialty;
import com.project.MedicalClinic.service.SpecialtyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/specialties")
public class SpecialtyController {

    private SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping("list")
    public String listSpecialties(Model theModel){

        List<Specialty> theSpecialties = specialtyService.findAll();

        theModel.addAttribute("specialties", theSpecialties);

        return "specialties/list-specialties";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        Specialty theSpecialty = new Specialty();

        theModel.addAttribute("specialty", theSpecialty);

        return "specialties/specialty-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("specialtyId") int theId, Model theModel){

        Specialty theSpecialty = specialtyService.findById(theId);

        // set specialty as a model attribute to pre-populate the form
        theModel.addAttribute("specialty", theSpecialty);

        return "specialties/specialty-form";
    }

    @PostMapping("/save")
    public String saveSpecialty(@ModelAttribute("specialty") Specialty theSpecialty) {

        specialtyService.save(theSpecialty);

        return "redirect:/specialties/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("specialtyId") int theid){

        specialtyService.deleteById(theid);

        return "redirect:/specialties/list";
    }
}
