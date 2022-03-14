package com.example.taskify.service;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.UserForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Role;
import com.example.taskify.exception.ResourceAlreadyExistsException;
import com.example.taskify.exception.ResourceNotFoundException;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.OrganizationRepository;
import com.example.taskify.repository.RoleRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser saveUser(AppUser user) {
        if (appUserRepository.findByEmail(user.getEmail()).isEmpty()) {
            log.info("Saving new user with email {}", user.getEmail());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return appUserRepository.save(user);
        } else {
            log.error("User with email {} already exists in database", user.getEmail());
            throw new ResourceAlreadyExistsException("User with email: " + user.getEmail() + " already exists");
        }
    }

    public AppUser getUserByEmail(String email) {
        return appUserRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("There is no user with such email:" + email));
    }

    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("There is no user with id = " + id));
    }

    public AppUser updateUserById(Long id, String firstName,
                                  String lastName, String email) {
        if (appUserRepository.findByEmail(email).isEmpty()) {
            AppUser user = getUserById(id);
            user.setName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            log.info("User with id {} updated successfully", id);
            return appUserRepository.save(user);
        } else
            throw new ResourceAlreadyExistsException("User with email: " + email + " already exists");
    }

    public void deleteUserById(Long id) {
        Optional<AppUser> user = appUserRepository.findById(id);
        if (user.isPresent()) {
            organizationRepository.findByName(user.get().getOrganizationName()).get().getAppUsers().remove(user.get());
            log.info("User with id {} deleted successfully", id);
            appUserRepository.deleteById(id);
        } else {
            log.error("There is no such user with id {} in database", id);
            throw new ResourceNotFoundException("There is no user with id = " + id + " in database");
        }
    }

    public AppUser createUser(CreateNewUserForm form) {
        AppUser user = new AppUser(form.getFirstName(), form.getLastName(), form.getEmail(), form.getPassword());
        user.setOrganizationName(form.getOrganization());
        user.getRoles().add(getRoleByName("ROLE_USER"));
        return saveUser(user);
    }

    public List<UserForm> getUsersOfOrganization(String email) {
        String orgName = getUserByEmail(email).getOrganizationName();
        List<UserForm> users = new ArrayList<>();
        appUserRepository.findAllByOrganizationName(orgName).forEach(
                user -> users.add(
                        new UserForm(user.getId(), user.getName(), user.getLastName(), user.getEmail())));
        log.info("Fetching all users from {}", orgName);
        return users;
    }

    public Role saveRole(Role role) {
        if (roleRepository.findByName(role.getName()).isEmpty()) {
            log.info("Fetching role with name {}", role.getName());
            return roleRepository.save(role);
        } else {
            log.error("There is no {} role in database", role.getName());
            throw new ResourceAlreadyExistsException(role.getName() + " already exists");
        }
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() ->
                new ResourceNotFoundException(name + " doesn't exists in database"));
    }

    public void addRoleToUser(String email, String roleName) {
        log.info("Adding role {} to user with email: {}", roleName, email);
        getUserByEmail(email).getRoles().add(getRoleByName(roleName));
    }

    public boolean isAdmin(String email) {
        List<String> roles = new ArrayList<>();
        getUserByEmail(email).getRoles().forEach(role -> roles.add(role.getName()));
        return roles.contains("ROLE_ADMIN");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);
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
