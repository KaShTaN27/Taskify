package com.example.taskify.controller;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.domain.Task;
import com.example.taskify.email.EmailSenderService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final UserService userService;
    private final EmailSenderService senderService;

    @GetMapping
    public ResponseEntity<Collection<Task>> getTasks(String email) {
        return ResponseEntity.ok().body(userService.getUser(email).getTasks());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTaskToUsers(@RequestBody AssignTaskForm form) {
        userService.saveTask(new Task(form.getTitle(),
                form.getDescription(),
                form.getDeadline(),
                form.getIsDone()));
        userService.addTaskToUsers(form.getEmails(), form.getTitle());
        form.getEmails().forEach(email -> senderService.sendSimpleEmail(email, form.getTitle(), form.getDescription(), form.getDeadline()));
        return ResponseEntity.ok("Task added to users!");
    }
}
