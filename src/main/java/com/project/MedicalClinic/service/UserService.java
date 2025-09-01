package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dto.UserProfileDTO;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.entity.User;
import com.project.MedicalClinic.user.WebUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> findAll();

    Page<User> findAlll(Pageable pageable);

    User findById(int theId);

    User basicSave(User theUser);

    UserProfileDTO getUserProfile(String username);

    User saveUser(User theUser);

    User saveUserDoctor(User theUser, Doctor theDoctor);

    public User findByUserName(String userName);

    User save(WebUser webUser);

    public void delete(User theUser);

    public void deleteById(int theId);

    boolean updatePassword(String username, String currentPassword, String newPassword, String confirmNewPassword);
}
