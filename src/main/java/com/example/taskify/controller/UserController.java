package com.example.taskify.controller;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.UpdateUserForm;
import com.example.taskify.controller.form.UserForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Task;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrganizationService organizationService;

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AppUser> getUser(Principal principal) {
        return ResponseEntity.ok().body(userService.getUser(principal.getName()));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createNewUser(@RequestBody CreateNewUserForm form) {
        userService.createUser(form);
        organizationService.addUserToOrganization(form.getOrganization(), form.getEmail());
        return ResponseEntity.ok("New user created!");
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Collection<Task>> getUserTasks(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUserById(id).getTasks());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUserById(@PathVariable Long id,
                                            @RequestBody UpdateUserForm form) {
        return ResponseEntity.ok().body(userService.updateUserById(id, form.getFirstName(),
                                                                   form.getLastName(), form.getEmail()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserForm>> getUsers(Principal principal) {
        return ResponseEntity.ok().body(userService.getUsersOfOrganization(principal.getName()));
    }
}
