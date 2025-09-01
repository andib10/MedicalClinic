package com.project.MedicalClinic.controller;

import com.project.MedicalClinic.dao.RoleDao;
import com.project.MedicalClinic.dto.UserProfileDTO;
import com.project.MedicalClinic.entity.User;
import com.project.MedicalClinic.service.UserService;
import com.project.MedicalClinic.user.PasswordModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private RoleDao roleDao;

    public UserController(UserService userService, RoleDao roleDao) {
        this.userService = userService;
        this.roleDao = roleDao;
    }


    @GetMapping("list")
    public String listUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size, Model theModel){

        Pageable pageable = PageRequest.of(page, size);
        Page<User> theUsers = userService.findAlll(pageable);

        theModel.addAttribute("users", theUsers);
        theModel.addAttribute("currentPage", page);
        theModel.addAttribute("totalPages", theUsers.getTotalPages());

        return "users/list-users";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel){

        User theUser = new User();

        theModel.addAttribute("user", theUser);

        return "users/user-form";
    }

    @GetMapping("/showFormForUpdate")
    public String showFormForUpdate(@RequestParam("userId") int theId, Model theModel){

        User theUser = userService.findById(theId);

        theModel.addAttribute("user", theUser);

        return "users/user-form-update";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User theUser) {

        // save admin user
        userService.saveUser(theUser);

        return "redirect:/users/list";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User theUser) {

        userService.basicSave(theUser);

        return "redirect:/users/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("userId") int theid){

        userService.deleteById(theid);

        return "redirect:/users/list";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(Model model) {
        model.addAttribute("pwdModel", new PasswordModel());
        return "change-password";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@Valid @ModelAttribute("pwdModel") PasswordModel pwdModel, BindingResult bindingResult, Principal principal, RedirectAttributes attributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pwdModel", pwdModel);
            return "change-password";
        }
        boolean success = userService.updatePassword(principal.getName(), pwdModel.getCurrentPassword(), pwdModel.getNewPassword(), pwdModel.getConfirmNewPassword());
        if (success) {
            attributes.addFlashAttribute("success", "Parolă actualizată cu succes!");
            return "redirect:/showMyLoginPage?logout";
        } else {
            attributes.addFlashAttribute("error", "Actualizarea parolei nu a reușit. Verifică informațiile introduse.");
        }
        return "redirect:/users/changePassword";
    }


    @GetMapping("/profile")
    public String showUserProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);

        UserProfileDTO userProfile = userService.getUserProfile(principal.getName());
        model.addAttribute("userProfile", userProfile);

        return "users/user-profile";
    }

    @PostMapping("/updateProfile")
    public String updateUserProfile(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            user.setUserName(principal.getName()); // ensure username is not changed
            userService.basicSave(user);
            redirectAttributes.addFlashAttribute("success", "Profil actualizat cu succes.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/users/profile";
    }

}
