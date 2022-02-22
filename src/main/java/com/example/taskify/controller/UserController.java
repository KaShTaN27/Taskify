package com.example.taskify.controller;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrganizationService organizationService;

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<AppUser> getUser(String email) {
        return ResponseEntity.ok().body(userService.getUser(email));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createNewUser(@RequestBody CreateNewUserForm form) {
        userService.saveUser(new AppUser(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPassword()));
        userService.addRoleToUser(form.getEmail(), "ROLE_USER");
        organizationService.addUserToOrganization(form.getOrganization(), form.getEmail());
        return ResponseEntity.ok("New user created!");
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<AppUser>> getUsers(String orgName) {
        return ResponseEntity.ok().body(userService.getUsers(orgName));
    }

    @GetMapping("/organization/members")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<String>> getMembersOfOrganization(String name) {
        List<String> emails = new ArrayList<>();
        organizationService.getOrganization(name).getAppUsers().forEach(user -> emails.add(user.getEmail()));
        return ResponseEntity.ok().body(emails);
    }
}
