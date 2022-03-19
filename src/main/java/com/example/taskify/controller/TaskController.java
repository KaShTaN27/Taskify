package com.example.taskify.controller;

import com.example.taskify.controller.form.AssignTaskForm;
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
                : userService.getUserByEmail(principal.getName()).getTasks());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> addTaskToUsers(@RequestBody AssignTaskForm form) {
        taskService.createTaskAndSendEmail(form);
        return ResponseEntity.ok("Task added to users!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok().body(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskById(@PathVariable Long id,
                                               Boolean isDone) {
        return ResponseEntity.ok().body(taskService.updateTaskById(id, isDone));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.ok().body("Task deleted successfully");
    }

    @GetMapping("/{taskId}/users")
    public ResponseEntity<Collection<AppUser>> getUsersWithSameTask(@PathVariable Long taskId) {
        return ResponseEntity.ok().body(taskService.getTaskById(taskId).getUsers());
    }
}
