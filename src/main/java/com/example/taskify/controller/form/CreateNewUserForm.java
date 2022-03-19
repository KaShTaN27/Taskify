package com.example.taskify.controller.form;

import lombok.Data;

@Data
public class CreateNewUserForm {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String organization;
}
