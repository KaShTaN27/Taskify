package com.example.taskify.mapper;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.domain.Task;

public class TaskMapper {
    public static Task mapTaskFromAssignTaskForm(AssignTaskForm form) {
        return new Task(form.getTitle(),
                        form.getDescription(),
                        form.getDeadline(),
                        form.getIsDone());
    }
}