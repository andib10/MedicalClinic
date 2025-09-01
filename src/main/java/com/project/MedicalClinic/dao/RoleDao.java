package com.project.MedicalClinic.dao;

import com.project.MedicalClinic.entity.Role;

import java.util.List;

public interface RoleDao {

    public Role findRoleByName(String theRoleName);

    List<Role> findAll();
}
