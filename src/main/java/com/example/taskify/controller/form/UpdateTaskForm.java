package com.example.taskify.controller.form;

import lombok.Data;

@Data
public class UpdateTaskForm {
    private String title;
    private String description;
    private String deadline;
}