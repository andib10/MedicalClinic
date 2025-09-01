package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
