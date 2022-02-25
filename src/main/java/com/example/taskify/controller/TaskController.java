package com.example.taskify.controller;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.controller.form.UpdateTaskForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Task;
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
@RequestMapping("/api/task")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class TaskController {

    private final UserService userService;
    private final TaskService taskService;
    private final OrganizationService organizationService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Collection<Task>> getTasks(Principal principal) {
        return ResponseEntity.ok().body(userService.isAdmin(principal.getName())
                ? organizationService.getOrganizationTasks(principal.getName())
                : userService.getUser(principal.getName()).getTasks());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> addTaskToUsers(@RequestBody AssignTaskForm form) {
        taskService.createTaskAndSendEmail(form);
        return ResponseEntity.ok("Task added to users!");
    }
}
