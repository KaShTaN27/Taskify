package com.example.taskify.controller;

import com.example.taskify.controller.form.LoginForm;
import com.example.taskify.controller.form.RegistrateOrganizationForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.mapper.OrganizationMapper;
import com.example.taskify.mapper.UserMapper;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.UserService;
import com.example.taskify.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import static org.springframework.http.HttpStatus.FORBIDDEN;

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
        try {
            authenticationManager.authenticate(UserMapper.mapPassAuthTokenFromLoginForm(form));
            AppUser user = userService.getUserByEmail(form.getEmail());
            String token = tokenProvider.generateToken(form.getEmail(), user.getRoles());
            Map<Object, Object> response = new HashMap<>();
            response.put("email", form.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(FORBIDDEN).body("Invalid password or email.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrateNewOrganization(@RequestBody RegistrateOrganizationForm form) {
        organizationService.saveOrganization(OrganizationMapper.mapOrgFromRegistrateOrganizationForm(form));
        userService.saveUser(UserMapper.mapUserFromRegistrateOrganizationForm(form));
        organizationService.addAdminToOrganization(form.getName(), form.getEmail());
        return ResponseEntity.ok("New organization registered!");
    }
}
