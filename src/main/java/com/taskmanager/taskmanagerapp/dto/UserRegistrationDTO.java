package com.taskmanager.taskmanagerapp.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String role;
}
