package com.example.taskify.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.RegistrateOrganizationForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.domain.Role;
import com.example.taskify.domain.Task;
import com.example.taskify.email.EmailSenderService;
import com.example.taskify.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    private final EmailSenderService senderService;

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/organizations")
    public ResponseEntity<List<Organization>> getOrganizations() {
        return ResponseEntity.ok().body(userService.getOrganizations());
    }

    @GetMapping("/user/tasks")
    public ResponseEntity<Collection<Task>> getTasks(String email) {
        return ResponseEntity.ok().body(userService.getUser(email).getTasks());
    }


    @PostMapping("/user/create")
    public ResponseEntity<?> createNewUser(@RequestBody CreateNewUserForm form) {
        userService.saveUser(new AppUser(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPassword()));
        userService.addRoleToUser(form.getEmail(), "ROLE_USER");
        userService.addUserToOrganization(form.getOrganization(), form.getEmail());
        return ResponseEntity.ok("New user created!");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrateNewOrganization(@RequestBody RegistrateOrganizationForm form) {
        userService.saveOrganization(new Organization(form.getName(), form.getPhoneNumber(), form.getAddress()));
        userService.saveUser(new AppUser(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPassword()));
        userService.addAdminToOrganization(form.getName(), form.getEmail());
        return ResponseEntity.ok("New organization registered!");
    }

    @PostMapping("/task/add")
    public ResponseEntity<?> addTaskToUsers(@RequestBody AssignTaskForm form) {
        userService.saveTask(new Task(form.getTitle(),
                                      form.getDescription(),
                                      form.getDeadline(),
                                      form.getIsDone()));
        userService.addTaskToUsers(form.getEmails(), form.getTitle());
        form.getEmails().forEach(email -> senderService.sendSimpleEmail(email, form.getTitle(), form.getDescription(), form.getDeadline()));
        return ResponseEntity.ok("Task added to users!");
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                AppUser user = userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
        return ResponseEntity.ok("Token refreshed!");
    }
}
