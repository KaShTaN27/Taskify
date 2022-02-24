package com.example.taskify.controller;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.domain.Task;
import com.example.taskify.email.EmailSenderService;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.TaskService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final UserService userService;
    private final EmailSenderService senderService;
    private final TaskService taskService;
    private final OrganizationService organizationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Collection<Task>> getTasks(Principal principal) {
        return ResponseEntity.ok().body(userService.isAdmin(principal.getName())
                ? organizationService.getOrganizationTasks(principal.getName())
                : userService.getUser(principal.getName()).getTasks());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> addTaskToUsers(@RequestBody AssignTaskForm form) {
        taskService.saveTask(new Task(form.getTitle(),
                form.getDescription(),
                form.getDeadline(),
                form.getIsDone()));
        taskService.addTaskToUsers(form.getEmails(), form.getTitle());
        form.getEmails().forEach(email -> senderService.sendSimpleEmail(email, form.getTitle(), form.getDescription(), form.getDeadline()));
        return ResponseEntity.ok("Task added to users!");
    }
}
