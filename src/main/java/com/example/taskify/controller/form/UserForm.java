package com.example.taskify.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class UserForm {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
