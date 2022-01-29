package com.example.taskify.controller.form;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AssignTaskForm {
    private String title;
    private String description;
    private String deadline;
    private Boolean isDone;
    private ArrayList<String> emails;
}
