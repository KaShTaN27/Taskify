package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.domain.Role;
import com.example.taskify.domain.Task;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.OrganizationRepository;
import com.example.taskify.repository.RoleRepository;
import com.example.taskify.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImp implements UserService , UserDetailsService {
    private static final String ADMIN = "ROLE_ADMIN";
    private final AppUserRepository appUserRepository;
    private final OrganizationRepository organizationRepository;
    private final TaskRepository taskRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void addAdminToOrganization(String organizationName, String userEmail) {
        addRoleToUser(userEmail, ADMIN);
        addUserToOrganization(organizationName, userEmail);
        log.info("Created organization {} with admin {}", organizationName, userEmail);
    }

    @Override
    public Organization saveOrganization(Organization organization) {
        log.info("Saving new organization {} to the database", organization.getName());
        return organizationRepository.save(organization);
    }

    @Override
    public Task saveTask(Task task) {
        log.info("Saving new task {} to the database", task.getTitle());
        return taskRepository.save(task);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("Saving new user with email {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return appUserRepository.save(user);
    }

    @Override
    public void addTaskToUsers(ArrayList<String> emails, String title) {
        Task task = taskRepository.findByTitle(title);
        emails.forEach( email -> {
            AppUser user = appUserRepository.findByEmail(email);
            if (user != null)
                user.getTasks().add(task);
        });
    }

    @Override
    public void addUserToOrganization(String organizationName, String email) {
        Organization organization = organizationRepository.findByName(organizationName);
        AppUser user = appUserRepository.findByEmail(email);
        log.info("Adding user with email {} to {}", email, organizationName);
        organization.getAppUsers().add(user);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        AppUser user = appUserRepository.findByEmail(email);
        Role role = roleRepository.findByName(roleName);
        log.info("Adding role {} to user with email: {}", roleName, email);
        user.getRoles().add(role);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Fetching all users");
        return appUserRepository.findAll();
    }

    @Override
    public AppUser getUser(String email) {
        log.info("Fetching user {}", email);
        return appUserRepository.findByEmail(email);
    }

    @Override
    public List<Organization> getOrganizations() {
        log.info("Fetching all organizations");
        return organizationRepository.findAll();
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
