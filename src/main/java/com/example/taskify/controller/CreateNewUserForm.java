package com.example.taskify.controller;

import lombok.Data;

@Data
public class CreateNewUserForm {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String organization;
}
