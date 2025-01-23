package com.adv.taskmanager.service;

import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.dtos.UserRegistrationDto;
import com.adv.taskmanager.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {


    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationDto registrationDto) {

        if (userRepo.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        userRepo.save(user);
    }

}
