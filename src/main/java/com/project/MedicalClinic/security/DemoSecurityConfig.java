package com.project.MedicalClinic.security;

import com.project.MedicalClinic.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class DemoSecurityConfig {

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationSuccessHandler customAuthenticationSuccessHandler) throws Exception {

        http.authorizeHttpRequests(configurer ->
                        configurer
//                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/users/profile/**", "/users/changePassword/**").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/fonts/**", "/images/**").permitAll()
                                .requestMatchers("/patient", "/patients/book", "/patients/appointments").hasRole("PATIENT")
                                .requestMatchers("/doctor", "/doctors/appointments", "/doctors/availabilities").hasRole("DOCTOR")
                                .requestMatchers("/doctors/list", "patients/list", "/appointments/list", "/specialties/list", "/availabilities/list", "/users/list", "/details/list").hasRole("ADMIN")
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers("/register/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/showMyLoginPage")
                                .loginProcessingUrl("/authenticateTheUser")
                                .successHandler(customAuthenticationSuccessHandler)
                                .permitAll()
                )
                .logout(logout -> logout.permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}
