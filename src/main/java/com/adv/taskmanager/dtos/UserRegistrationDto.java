package com.adv.taskmanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {
    private String username;
    private String email;
    private String password;
    private boolean emailVerified;
    private String oauthProvider;
    private String oauthId;
}
