package com.example.taskify.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailForm {
    private String toEmail;
    private String title;
    private String description;
    private String deadline;
}
