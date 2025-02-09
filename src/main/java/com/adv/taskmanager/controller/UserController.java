package com.adv.taskmanager.controller;


import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.dtos.UserDto;
import com.adv.taskmanager.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserDto("Unauthorized", "access denied."));
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));

        UserDto userDto = new UserDto(user.getUsername(), user.getEmail());
        return ResponseEntity.ok(userDto);
    }
}
