package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dao.*;
import com.project.MedicalClinic.dto.UserProfileDTO;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.entity.Patient;
import com.project.MedicalClinic.entity.User;
import com.project.MedicalClinic.user.WebUser;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService{

    private UserDao userDao;

    private RoleDao roleDao;

    private BCryptPasswordEncoder passwordEncoder;

    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private EntityManager entityManager;
    private UserRepository userRepository;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder,
                           PatientRepository patientRepository, DoctorRepository doctorRepository, EntityManager entityManager, UserRepository userRepository) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public Page<User> findAlll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(int theId) {
        Optional<User> result = Optional.ofNullable(userDao.findById(theId));

        User theUser = null;

        if (result.isPresent()) {
            theUser = result.get();
        }
        else {
            throw new RuntimeException("Did not find user id - " + theId);
        }

        return theUser;

    }

    @Override
    public User basicSave(User theUser) {

        User existingUser = userDao.findById(theUser.getId());

        existingUser.setUserName(theUser.getUserName());
        existingUser.setEmail(theUser.getEmail());
        existingUser.setFirstName(theUser.getFirstName());
        existingUser.setLastName(theUser.getLastName());
        // update without changing password

        return userDao.saveUser(existingUser);
    }

    @Override
    public User saveUser(User theUser) {
        User existingUser = userDao.findByUserName(theUser.getUserName());
        if (existingUser != null) {
            throw new IllegalStateException("User already exists.");
        }
        // default password for a new admin user
        // to do: set random temporary password (sent via email) or force password change on first login, not hardcoded
        theUser.setPassword("$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K");
        theUser.setEnabled(true);
        theUser.setRole(roleDao.findRoleByName("ROLE_ADMIN"));

        return userDao.saveUser(theUser);
    }

    @Override
    public User saveUserDoctor(User theUser, Doctor theDoctor){
        User existingUser = userDao.findByUserName(theUser.getUserName());
        if (existingUser != null) {
            throw new IllegalStateException("User already exists.");
        }

        theUser.setUserName(theDoctor.getUser().getUserName());
        theUser.setEmail(theDoctor.getUser().getEmail());
        theUser.setFirstName(theDoctor.getUser().getFirstName());
        theUser.setLastName(theDoctor.getUser().getLastName());
        // default password for a new doc user
        // to do: set random temporary password (sent via email) or force password change on first login, not hardcoded
        theUser.setPassword("$2a$04$eFytJDGtjbThXa80FyOOBuFdK2IwjyWefYkMpiBEFlpBwDH.5PM0K");
        theUser.setEnabled(true);
        theUser.setRole(roleDao.findRoleByName("ROLE_DOCTOR"));
        User savedUser = userDao.saveUser(theUser);
        theDoctor.setUser(savedUser);

        return savedUser;
    }


    @Override
    public User findByUserName(String userName) {
        // check the database if the user already exists
        return userDao.findByUserName(userName);
    }

    @Override
    public User save(WebUser webUser) {
        User user = new User();

        // assign user details to the user object
        user.setUserName(webUser.getUserName());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setEmail(webUser.getEmail());
        user.setFirstName(webUser.getFirstName());
        user.setLastName(webUser.getLastName());
        user.setEnabled(true);
        // give user default role of "patient"
        user.setRole(roleDao.findRoleByName("ROLE_PATIENT"));

        // Check if the user already exists to prevent duplicates
        User existingUser = userDao.findByUserName(user.getUserName());
        if (existingUser != null) {
            throw new IllegalStateException("User already exists.");
        }

        // when a new user is created also a new patient will be created linked to that user (for user registration)


        logger.info("Creating patient linked to user...");
        Patient newPatient = new Patient();
        newPatient.setBirthDate(webUser.getBirthDate());
        newPatient.setCNP(webUser.getCNP());
        newPatient.setPhoneNumber(webUser.getPhoneNumber());
        newPatient.setUser(user);

        patientRepository.save(newPatient);
        logger.info("Patient saved with linked user ID: " + newPatient.getUser().getId());

        return user;
    }

    @Override
    public void delete(User theUser) {
        userDao.delete(theUser);
    }

    @Override
    public void deleteById(int theId) {
        userDao.deleteById(theId);
    }

    /*public void deleteById(int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Doctor doctor = user.getDoctor();

            if (doctor != null) {
                List<Availability> availabilities = doctor.getAvailabilities();
                for (Availability availability : availabilities) {
                    availability.getDoctors().remove(doctor);
                    if (availability.getDoctors().isEmpty()) { availabilityRepository.delete(availability); }
                    else { availabilityRepository.save(availability); }
                }
            }
            userRepository.delete(user);
        }
    }*/

    @Override
    public boolean updatePassword(String username, String currentPassword, String newPassword, String confirmNewPassword) {
        User user = userDao.findByUserName(username);
        if (user != null && passwordEncoder.matches(currentPassword, user.getPassword())) {
            if (newPassword.equals(confirmNewPassword)) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userDao.save(user);
                return true;
            }
        }
        return false;

    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userDao.findByUserName(userName);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());


        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                Collections.singletonList(authority));
    }


    @Override
    public UserProfileDTO getUserProfile(String username) {
        User user = userDao.findByUserName(username);
        UserProfileDTO profile = new UserProfileDTO(user);

        if (user.getRole().getName().equals("ROLE_DOCTOR")) {
            Doctor doctor = doctorRepository.findByUserId(user.getId());
            profile.setHireDate(doctor.getHiringDate());

        } else if (user.getRole().getName().equals("ROLE_PATIENT")) {
            Patient patient = patientRepository.findByUserId(user.getId());
            profile.setBirthDate(patient.getBirthDate());
            profile.setPhoneNumber(patient.getPhoneNumber());
            profile.setCNP(patient.getCNP());
        }

        return profile;
    }

}
