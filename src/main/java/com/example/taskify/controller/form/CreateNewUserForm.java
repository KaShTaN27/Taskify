package com.example.taskify.controller.form;

import lombok.Data;

@Data
public class CreateNewUserForm {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String organization;

    public CreateNewUserForm(String firstName, String lastName, String email, String password, String organization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.organization = organization;
    }
}
