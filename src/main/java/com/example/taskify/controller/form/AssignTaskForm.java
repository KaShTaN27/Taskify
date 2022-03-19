package com.example.taskify.controller.form;

import lombok.Data;

import java.util.List;

@Data
public class AssignTaskForm {
    private final String title;
    private final String description;
    private final String deadline;
    private final Boolean isDone;
    private final List<String> emails;
}
