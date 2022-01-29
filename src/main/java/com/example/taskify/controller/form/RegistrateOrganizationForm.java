package com.example.taskify.controller.form;

import lombok.Data;

@Data
public class RegistrateOrganizationForm {
    private String name;
    private String phoneNumber;
    private String address;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
