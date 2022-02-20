package com.example.taskify.service;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.LoginForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Role;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.RoleRepository;
import com.example.taskify.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;

    private final OrganizationService organizationService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AppUser saveUser(AppUser user) {
        if (appUserRepository.findByEmail(user.getEmail()) == null) {
            log.info("Saving new user with email {}", user.getEmail());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return appUserRepository.save(user);
        } else {
            log.error("User with email {} already exists in database", user.getEmail());
            return user;
        }
    }

    public AppUser getUser(String email) {
        AppUser user = appUserRepository.findByEmail(email);
        if (user != null) {
            log.info("Fetching user with email: {}", email);
            return user;
        } else {
            log.error("There is no such user with email: {}", email);
            return new AppUser();
        }
    }

    public AppUser updateUserById(Long id, String firstName,
                                  String lastName, String email, String password) {
        Optional<AppUser> optionalAppUser = appUserRepository.findById(id);
        if (optionalAppUser.isPresent()) {
            AppUser user = optionalAppUser.get();
            user.setName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            log.info("User with id {} updated successfully", id);
            return appUserRepository.save(user);
        } else {
            log.error("There is no such user with id {} in database", id);
            return new AppUser();
        }
    }

    public void deleteUser(Long id) {
        if (appUserRepository.findById(id).isPresent()) {
            log.info("User with id {} deleted successfully", id);
            appUserRepository.deleteById(id);
        } else {
            log.error("There is no such user with id {} in database", id);
        }
    }

    public Map<Object, Object> authenticateUser(LoginForm form) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword()));
        AppUser user = getUser(form.getEmail());
        String token = tokenProvider.generateToken(form.getEmail(), user.getRoles());
        Map<Object, Object> response = new HashMap<>();
        response.put("email", form.getEmail());
        response.put("token", token);
        return response;
    }

    public void createUser(CreateNewUserForm form) {
        AppUser user = new AppUser(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPassword());
        user.setOrganizationName(form.getOrganization());
        saveUser(user);
        addRoleToUser(form.getEmail(), "ROLE_USER");
        organizationService.addUserToOrganization(form.getOrganization(), user.getEmail());
    }

    public List<AppUser> getUsers(String orgName) {
        log.info("Fetching all users from {}", orgName);
        return appUserRepository.findAllByOrganizationName(orgName);
    }

    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    public void addRoleToUser(String email, String roleName) {
        AppUser user = appUserRepository.findByEmail(email);
        Role role = roleRepository.findByName(roleName);
        log.info("Adding role {} to user with email: {}", roleName, email);
        user.getRoles().add(role);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found in database");
            throw new UsernameNotFoundException("User with email " + email + " not found");
        } else {
            log.info("User with email {} found in database", email);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
            return new User(user.getEmail(), user.getPassword(), authorities);
        }
    }
}
