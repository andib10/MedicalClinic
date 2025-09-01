package com.project.MedicalClinic.service;

import com.project.MedicalClinic.dto.UserProfileDTO;
import com.project.MedicalClinic.entity.Doctor;
import com.project.MedicalClinic.entity.Patient;
import com.project.MedicalClinic.entity.Role;
import com.project.MedicalClinic.entity.User;
import com.project.MedicalClinic.user.WebUser;
import com.project.MedicalClinic.dao.PatientRepository;
import com.project.MedicalClinic.dao.RoleDao;
import com.project.MedicalClinic.dao.UserDao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserDao userDao;
    @Mock private RoleDao roleDao;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private PatientRepository patientRepository;


    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setUserName("john");
        user.setPassword("encodedPass");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        Role role = new Role();
        role.setName("ROLE_PATIENT");
        user.setRole(role);
    }
    @Test
    void findAll() {
        when(userDao.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertEquals(1, result.size());
        verify(userDao).findAll();
    }


    @Test
    void findById() {
        when(userDao.findById(1)).thenReturn(user);

        User result = userService.findById(1);

        assertNotNull(result);
        assertEquals("john", result.getUserName());
    }

    @Test
    void saveAdminUser() {
        when(userDao.findByUserName("john")).thenReturn(null);
        when(roleDao.findRoleByName("ROLE_ADMIN")).thenReturn(new Role("ROLE_ADMIN"));
        when(userDao.saveUser(any(User.class))).thenReturn(user);

        User result = userService.saveUser(user);

        assertNotNull(result);
        assertEquals("john", result.getUserName());
        verify(userDao).saveUser(user);
    }

    @Test
    void saveDoctorUser() {
        Doctor doctor = new Doctor();
        User doctorUser = new User();
        doctorUser.setUserName("doc");
        doctorUser.setEmail("doc@mail.com");
        doctorUser.setFirstName("Doc");
        doctorUser.setLastName("Tor");
        doctor.setUser(doctorUser);

        when(userDao.findByUserName("doc")).thenReturn(null);
        when(roleDao.findRoleByName("ROLE_DOCTOR")).thenReturn(new Role("ROLE_DOCTOR"));
        when(userDao.saveUser(any(User.class))).thenReturn(doctorUser);

        User result = userService.saveUserDoctor(new User(), doctor);

        assertNotNull(result);
        assertEquals("doc", result.getUserName());
    }

    @Test
    void testSaveFromWebUser() {
        WebUser webUser = new WebUser();
        webUser.setUserName("newuser");
        webUser.setPassword("rawPass");
        webUser.setEmail("new@mail.com");
        webUser.setFirstName("New");
        webUser.setLastName("User");
        webUser.setCNP("123");
        webUser.setBirthDate(Date.valueOf(LocalDate.of(2000, 1, 1)));
        webUser.setPhoneNumber("0712345678");

        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(roleDao.findRoleByName("ROLE_PATIENT")).thenReturn(new Role("ROLE_PATIENT"));
        when(userDao.findByUserName("newuser")).thenReturn(null);

        User result = userService.save(webUser);

        assertNotNull(result);
        assertEquals("newuser", result.getUserName());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePassword() {
        when(userDao.findByUserName("john")).thenReturn(user);
        when(passwordEncoder.matches("oldPass", "encodedPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        boolean result = userService.updatePassword("john", "oldPass", "newPass", "newPass");

        assertTrue(result);
        verify(userDao).save(user);
    }

    @Test
    void updatePasswordFail() {
        when(userDao.findByUserName("john")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        boolean result = userService.updatePassword("john", "wrong", "new", "new");

        assertFalse(result);
    }

    @Test
    void loadUserByUsername() {
        when(userDao.findByUserName("john")).thenReturn(user);

        UserDetails details = userService.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(userDao.findByUserName("notfound")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("notfound"));
    }

    @Test
    void getUserProfile() {
        Patient patient = new Patient();
        patient.setCNP("123");
        patient.setPhoneNumber("0712345678");
        patient.setBirthDate(Date.valueOf(LocalDate.of(1990, 5, 15)));

        when(userDao.findByUserName("john")).thenReturn(user);
        when(patientRepository.findByUserId(1)).thenReturn(patient);

        UserProfileDTO profile = userService.getUserProfile("john");

        assertEquals("123", profile.getCNP());
        assertEquals("0712345678", profile.getPhoneNumber());
    }

    @Test
    void deleteById() {
        userService.deleteById(42);
        verify(userDao).deleteById(42);
    }
}