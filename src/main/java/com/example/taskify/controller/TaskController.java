package com.example.taskify.controller;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.domain.Task;
import com.example.taskify.email.EmailSenderService;
import com.example.taskify.service.TaskService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import java.util.Collection;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final UserService userService;
    private final EmailSenderService senderService;
    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Collection<Task>> getTasks(String email) {
        return ResponseEntity.ok().body(userService.getUser(email).getTasks());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> addTaskToUsers(@RequestBody AssignTaskForm form) {
        taskService.addTaskToUsers(form);
        return ResponseEntity.ok("Task added to users!");
    }
}
