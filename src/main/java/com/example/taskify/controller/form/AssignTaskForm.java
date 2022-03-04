package com.example.taskify.controller.form;

import lombok.Data;

import java.util.List;

@Data
public class AssignTaskForm {
    private String title;
    private String description;
    private String deadline;
    private Boolean isDone;
    private List<String> emails;

    public AssignTaskForm(String title, String description, String deadline, Boolean isDone, List<String> emails) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isDone = isDone;
        this.emails = emails;
    }
}
