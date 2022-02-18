package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Role;
import com.example.taskify.repository.AppUserRepository;
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
    private final PasswordEncoder passwordEncoder;

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
