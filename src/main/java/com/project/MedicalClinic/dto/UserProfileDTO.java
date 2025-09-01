package com.project.MedicalClinic.dto;

import com.project.MedicalClinic.entity.User;

import java.util.Date;

public class UserProfileDTO {

    private int id;
    private String username;
    private String email;
    private String role;
    // Role-specific fields
    private Date hireDate;  // For Doctors
    private Date birthDate; // For Patients
    private String phoneNumber;  // For Patients
    private String CNP;          // For Patients

    public UserProfileDTO() {}

    public UserProfileDTO(User user) {
        this.id = user.getId();
        this.username = user.getUserName();
        this.email = user.getEmail();
        this.role = user.getRole().getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }
}
