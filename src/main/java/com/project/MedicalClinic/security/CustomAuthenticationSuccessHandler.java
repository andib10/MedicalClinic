package com.project.MedicalClinic.security;

import com.project.MedicalClinic.entity.User;
import com.project.MedicalClinic.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private UserService userService;

    public CustomAuthenticationSuccessHandler (UserService theUserService) {
        userService = theUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        System.out.println("In customAuthenticationSuccessHandler");

        String userName = authentication.getName();

        System.out.println("userName=" + userName);

        User theUser = userService.findByUserName(userName);

        // now place in the session
        HttpSession session = request.getSession();
        session.setAttribute("user", theUser);

        // forward to home page
       // response.sendRedirect(request.getContextPath() + "/");


        // Redirect based on the user role
        String redirectUrl = request.getContextPath();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String roleName = authority.getAuthority();
            if (roleName.equals("ROLE_ADMIN")) {
                redirectUrl += "/admin";
                break;
            } else if (roleName.equals("ROLE_DOCTOR")) {
                redirectUrl += "/doctor";
                break;
            } else if (roleName.equals("ROLE_PATIENT")) {
                redirectUrl += "/patient";
                break;
            }
        }

        response.sendRedirect(redirectUrl);

    }

}

