package com.example.taskify.service;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.controller.form.UserForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Role;
import com.example.taskify.exception.ResourceAlreadyExistsException;
import com.example.taskify.exception.ResourceNotFoundException;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final AppUser TEST_USER = new AppUser(1L, "name", "lastName", "email",
            "password", new ArrayList<>(), new ArrayList<>(), "organization");

    private static final Role TEST_ROLE = new Role(1L, "ROLE_ADMIN");

    @Test
    void saveUser_IfDoesNotExists() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_USER.getPassword())).thenReturn("dfgdgfd");
        when(appUserRepository.save(TEST_USER)).thenReturn(TEST_USER);

        AppUser newUser = userService.saveUser(TEST_USER);
        assertEquals(TEST_USER, newUser);
    }

    @Test
    void saveUser_IfAlreadyExistsInDatabase() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.saveUser(TEST_USER));
    }

    @Test
    void getUser_IfExistsInDatabase() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));

        AppUser newUser = userService.getUserByEmail(TEST_USER.getEmail());
        assertEquals(TEST_USER.getEmail(), newUser.getEmail());
    }

    @Test
    void getUser_IfDoesNotExistsInDatabase() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(TEST_USER.getEmail()));
    }

    @Test
    void getUserById_IfDoesNotExistsInDatabase() {
        when(appUserRepository.findById(TEST_USER.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(TEST_USER.getId()));
    }

    @Test
    void updateUserById_IfExistsInDatabase() {
        AppUser updatedUser = TEST_USER;
        updatedUser.setName("NewName");
        updatedUser.setEmail("NewEmail");
        updatedUser.setLastName("NewLastName");

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(TEST_USER));
        when(appUserRepository.save(updatedUser)).thenReturn(updatedUser);

        AppUser newUser = userService.updateUserById(TEST_USER.getId(), "NewEmail");
        assertEquals(TEST_USER.getId(), newUser.getId());
        assertEquals(TEST_USER.getPassword(), newUser.getPassword());
        assertNotEquals("name", newUser.getName());
        assertNotEquals("lastName", newUser.getLastName());
        assertNotEquals("email", newUser.getEmail());
    }

    @Test
    void updateUserById_IfDoesNotExistsInDatabase() {
        when(appUserRepository.findById(TEST_USER.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUserById(TEST_USER.getId(), "NewEmail"));
    }

    @Test
    void deleteUserById_IfExistsInDatabase() {
        when(appUserRepository.findById(TEST_USER.getId())).thenReturn(Optional.of(TEST_USER));

        userService.deleteUserById(TEST_USER.getId());
        verify(appUserRepository, times(1)).deleteById(TEST_USER.getId());
    }

    @Test
    void deleteUserById_IfDoesNotExistsInDatabase() {
        when(appUserRepository.findById(TEST_USER.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUserById(TEST_USER.getId()));
    }

    @Test
    void userShouldExistsAndHaveRoleAdmin() {
        TEST_USER.setRoles(List.of(TEST_ROLE));

        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));
        assertTrue(userService.isAdmin(TEST_USER.getEmail()));
    }

    @Test
    void userShouldExistsAndDoesNotHaveRoleAdmin() {
        TEST_ROLE.setName("ROLE_USER");
        TEST_USER.setRoles(List.of(TEST_ROLE));

        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));
        assertFalse(userService.isAdmin(TEST_USER.getEmail()));
    }

    @Test
    void userShouldNotExistsAndHaveRoleAdmin() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.isAdmin(TEST_USER.getEmail()));
    }

    @Test
    void addRoleToUser_IfUserAndRoleExists() {
        TEST_USER.setRoles(new ArrayList<>());
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));
        when(roleRepository.findByName(TEST_ROLE.getName())).thenReturn(Optional.of(TEST_ROLE));

        userService.addRoleToUser(TEST_USER.getEmail(), TEST_ROLE.getName());
        assertTrue(TEST_USER.getRoles().contains(TEST_ROLE));
    }

    @Test
    void createUser_IfDoesNotExistsInDatabase() {
        CreateNewUserForm form = new CreateNewUserForm(TEST_USER.getName(), TEST_USER.getLastName(),
                TEST_USER.getEmail(), TEST_USER.getPassword(), TEST_USER.getOrganizationName());
        TEST_ROLE.setName("ROLE_USER");
        TEST_USER.setId(null);
        TEST_USER.setRoles(List.of(TEST_ROLE));

        when(roleRepository.findByName(TEST_ROLE.getName())).thenReturn(Optional.of(TEST_ROLE));
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_USER.getPassword())).thenReturn(TEST_USER.getPassword());
        when(appUserRepository.save(TEST_USER)).thenReturn(TEST_USER);

        AppUser newUser = userService.createUser(form);
        assertTrue(newUser.getRoles().contains(TEST_ROLE));
    }

    @Test
    void createUser_IfAlreadyExistsInDatabase() {
        CreateNewUserForm form = new CreateNewUserForm(TEST_USER.getName(), TEST_USER.getLastName(),
                TEST_USER.getEmail(), TEST_USER.getPassword(), TEST_USER.getOrganizationName());
        TEST_ROLE.setName("ROLE_USER");
        TEST_USER.setId(null);
        TEST_USER.setRoles(List.of(TEST_ROLE));

        when(roleRepository.findByName(TEST_ROLE.getName())).thenReturn(Optional.of(TEST_ROLE));
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(form));
    }

    @Test
    void getUsersOfOrganization_IfUserAndOrganizationExists_AndOrganizationContainsUsers() {
        List<AppUser> users = new ArrayList<>();
        users.add(TEST_USER);
        users.add(TEST_USER);

        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));
        when(appUserRepository.findAllByOrganizationName(TEST_USER.getOrganizationName())).thenReturn(users);
        List<UserForm> userForms = userService.getUsersOfOrganization(TEST_USER.getEmail());
        assertNotNull(userForms);
    }

    @Test
    void saveRole_IfAlreadyExistsInDatabase() {
        when(roleRepository.findByName(TEST_ROLE.getName())).thenReturn(Optional.of(TEST_ROLE));

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.saveRole(TEST_ROLE));
    }

    @Test
    void saveRole_IfDoesNotExistsInDatabase() {
        when(roleRepository.findByName(TEST_ROLE.getName())).thenReturn(Optional.empty());
        when(roleRepository.save(TEST_ROLE)).thenReturn(TEST_ROLE);

        Role newRole = userService.saveRole(TEST_ROLE);
        assertEquals(newRole, TEST_ROLE);
    }

    @Test
    void loadUserByUsername_IfUserDoesNotExists() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(TEST_USER.getEmail()));
    }

    @Test
    void loadUserByUsername_IfUserExists() {
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));
        TEST_USER.setRoles(List.of(TEST_ROLE));

        UserDetails userDetails = userService.loadUserByUsername(TEST_USER.getEmail());
        assertEquals(TEST_USER.getEmail(), userDetails.getUsername());
        assertEquals(TEST_USER.getPassword(), userDetails.getPassword());
    }
}