package com.matosic.Facebook.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
