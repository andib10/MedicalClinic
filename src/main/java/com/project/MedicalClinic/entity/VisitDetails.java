package com.project.MedicalClinic.entity;

import jakarta.persistence.*;

@Entity
@Table(name="visit_details")
public class VisitDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="diagnosis")
    private String diagnosis;

    @Column(name="procedure_completed")
    private String procedureCompleted;

    @Column(name="recommendation")
    private String recommendation;

    @OneToOne
    @JoinColumn(name="appointment_id")
    private Appointment appointment;

    public VisitDetails() {}

    public VisitDetails(String diagnosis, String procedureCompleted, String recommendation, Appointment appointment) {
        this.diagnosis = diagnosis;
        this.procedureCompleted = procedureCompleted;
        this.recommendation = recommendation;
        this.appointment = appointment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getProcedureCompleted() {
        return procedureCompleted;
    }

    public void setProcedureCompleted(String procedureCompleted) {
        this.procedureCompleted = procedureCompleted;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }


    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    @Override
    public String toString() {
        return "VisitDetails{" +
                "id=" + id +
                ", diagnosis='" + diagnosis + '\'' +
                ", procedureCompleted='" + procedureCompleted + '\'' +
                ", recommendation='" + recommendation + '\'' +
                ", appointment=" + appointment +
                '}';
    }
}
