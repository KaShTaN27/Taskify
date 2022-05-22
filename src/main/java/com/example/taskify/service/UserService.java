package com.example.taskify.service;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.UserForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Role;
import com.example.taskify.exception.ResourceAlreadyExistsException;
import com.example.taskify.exception.ResourceNotFoundException;
import com.example.taskify.mapper.UserMapper;
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

import java.util.Collection;
import java.util.List;

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
        if (!appUserRepository.existsByEmail(user.getEmail())) {
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

    public AppUser updateUserById(Long id, String password) {
        AppUser user = getUserById(id);
        user.setPassword(passwordEncoder.encode(password));
        log.info("User with id {} updated successfully", id);
        return appUserRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (appUserRepository.existsById(id)) {
            AppUser user = getUserById(id);
            organizationRepository.findByName(user.getOrganizationName()).get().getAppUsers().remove(user);
            log.info("User with id {} deleted successfully", id);
            appUserRepository.deleteById(id);
        } else {
            log.error("There is no such user with id {} in database", id);
            throw new ResourceNotFoundException("There is no user with id = " + id + " in database");
        }
    }

    public AppUser createUser(CreateNewUserForm form) {
        AppUser user = UserMapper.mapUserFromCreateNewUserForm(form);
        user.getRoles().add(getRoleByName("ROLE_USER"));
        return saveUser(user);
    }

    public List<UserForm> getUsersOfOrganization(String email) {
        String orgName = getUserByEmail(email).getOrganizationName();
        log.info("Fetching all users from {}", orgName);
        return appUserRepository.findAllByOrganizationName(orgName).stream()
                .map(UserMapper::mapUserToUserForm)
                .toList();
    }

    public Role saveRole(Role role) {
        if (!roleRepository.existsByName(role.getName())) {
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
        return getUserByEmail(email).getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);
        if (user == null) {
            log.error("User not found in database");
            throw new UsernameNotFoundException("User with email " + email + " not found");
        } else {
            log.info("User with email {} found in database", email);
            Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .toList();
            return new User(user.getEmail(), user.getPassword(), authorities);
        }
    }
}
