package com.adv.taskmanager.controller;

import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class EmailVerificationController {

    private final UserRepo userRepo;

    public EmailVerificationController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        User user = userRepo.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or Expired Verification Token."));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        return ResponseEntity.ok("Email Successfully verified!");

    }
}
