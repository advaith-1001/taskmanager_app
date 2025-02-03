package com.adv.taskmanager.service;

import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.dtos.UserRegistrationDto;
import com.adv.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {


    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public RegistrationService(UserRepo userRepo, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void registerUser(UserRegistrationDto registrationDto) {
        if (userRepo.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());

        // Handle different cases for password vs OAuth
        if (registrationDto.getOauthProvider() == null || registrationDto.getOauthProvider().isEmpty()) {


            String token = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            user.setVerificationToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(24));
            user.setEmailVerified(false);
            userRepo.save(user);

            System.out.println("Attempting to send email...");
            String verificationUrl = "http://localhost:8080/auth/verify-email?token=" + token;
            emailService.sendVerificationEmail(user.getEmail(), verificationUrl);
        } else {
            // OAuth2 users: No password needed, auto-verify email
            user.setOauthProvider(registrationDto.getOauthProvider());
            user.setOauthId(registrationDto.getOauthId());
            user.setEmailVerified(true);
            userRepo.save(user);
        }
    }
}
