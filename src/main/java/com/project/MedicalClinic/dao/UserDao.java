package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.User;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findById(int theId);

    User saveUser(User theUser);

    User findByUserName(String userName);

    User save(User theUser);

    public void delete(User theUser);

    public void deleteById(int theId);
}
