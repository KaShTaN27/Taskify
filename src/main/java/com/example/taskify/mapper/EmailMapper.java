package com.example.taskify.mapper;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.controller.form.EmailForm;

public class EmailMapper {
    public static EmailForm mapEmailFormFromAssignTaskForm(String email, AssignTaskForm form) {
        return new EmailForm(email,
                             form.getTitle(),
                             form.getDescription(),
                             form.getDeadline());
    }
}
