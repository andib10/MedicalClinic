package com.project.MedicalClinic.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="birthDate")
    private Date birthDate;

    @Column(name="CNP")
    private String CNP;

    @Column(name="phone_number")
    private String phoneNumber;

    @OneToOne(cascade = {CascadeType.PERSIST})

    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;

    public Patient() {}

    public Patient(Date birthDate, String CNP, String phoneNumber, User user) {
        this.birthDate = birthDate;
        this.CNP = CNP;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



}
