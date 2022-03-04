package com.example.taskify.controller;

import com.example.taskify.controller.form.LoginForm;
import com.example.taskify.controller.form.RegistrateOrganizationForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.security.JwtTokenProvider;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OrganizationService organizationService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword()));
        AppUser user = userService.getUser(form.getEmail());
        String token = tokenProvider.generateToken(form.getEmail(), user.getRoles());
        Map<Object, Object> response = new HashMap<>();
        response.put("email", form.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrateNewOrganization(@RequestBody RegistrateOrganizationForm form) {
        Organization organization = new Organization(form.getName(), form.getPhoneNumber(), form.getAddress());
        organizationService.saveOrganization(organization);
        AppUser user = new AppUser(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPassword());
        user.setOrganizationName(organization.getName());
        userService.saveUser(user);
        organizationService.addAdminToOrganization(form.getName(), form.getEmail());
        return ResponseEntity.ok("New organization registered!");
    }
}
