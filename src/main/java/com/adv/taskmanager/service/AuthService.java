package com.adv.taskmanager.service;

import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.controller.LoginRequest;
import com.adv.taskmanager.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public String authenticate(LoginRequest loginRequest) {
        // Authenticate user using Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // Fetch user details after authentication
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());

        // Generate JWT token
        return jwtService.generateToken(userDetails.getUsername());
    }
}
