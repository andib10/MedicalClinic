package com.project.MedicalClinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="hiring_date")
    private Date hiringDate;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    @JsonIgnore
    private Specialty specialty;

    @OneToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

   /* the default JSON processor used by Spring Boot, tries to serialize an object containing recursive references.
    This happens when two or more objects reference each other in a circular way.
    @JsonIgnore annotation to prevent one side of the bidirectional relationship from being serialized.*/


    @ManyToMany(mappedBy = "doctors")
    @JsonIgnore
    private List<Availability> availabilities;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;

    public Doctor() {}

    public Doctor(Date hiringDate, Specialty specialty, User user) {
        this.hiringDate = hiringDate;
        this.specialty = specialty;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(Date hiringDate) {
        this.hiringDate = hiringDate;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public List<Availability> getAvailabilities() {
        return availabilities;
    }

    public void setAvailabilities(List<Availability> availabilities) {
        this.availabilities = availabilities;
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

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", hiringDate=" + hiringDate +
                ", specialty=" + specialty +
                ", user=" + user +
                ", availabilities=" + availabilities +
                ", appointments=" + appointments +
                '}';
    }
}
