package com.project.MedicalClinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="slot_start")
    private LocalDateTime timeSlot;


    @ManyToMany
    @JoinTable(
            name = "doctor_availabilities",
            joinColumns = @JoinColumn(name = "availability_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    @JsonIgnore
    private List<Doctor> doctors = new ArrayList<>();


    @Transient
    private List<Integer> doctorIds = new ArrayList<>();

    public List<Integer> getDoctorIds() {
        return doctorIds;
    }

    public void setDoctorIds(List<Integer> doctorIds) {
        this.doctorIds = doctorIds;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public Availability() {}

    public Availability(Doctor doctor, LocalDateTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public LocalDateTime getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(LocalDateTime timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "id=" + id +
                ", timeSlot=" + timeSlot +
                '}';
    }
}
