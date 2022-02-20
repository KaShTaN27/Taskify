package com.example.taskify.controller;

import com.example.taskify.controller.form.LoginForm;
import com.example.taskify.controller.form.RegistrateOrganizationForm;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OrganizationService organizationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form) {
        return ResponseEntity.ok(userService.authenticateUser(form));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrateNewOrganization(@RequestBody RegistrateOrganizationForm form) {
        organizationService.registerOrganization(form);
        return ResponseEntity.ok("New organization registered!");
    }
}
