package com.example.taskify.service;

import com.example.taskify.controller.form.CreateNewUserForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Role;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void saveUser_IfDoesNotExists() {
        AppUser user = new AppUser("Roker", "Rokerovich", "roker@gmail.com", "123456");

        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("dfgdgfd");
        when(appUserRepository.save(user)).thenReturn(user);

        AppUser newUser = userService.saveUser(user);
        assertEquals(user, newUser);
    }

    @Test
    void saveUser_IfAlreadyExistsInDatabase() {
        AppUser user = new AppUser("Danik", "Korzun", "korzundanik@gmail.com", "123456");

        when(appUserRepository.findByEmail("korzundanik@gmail.com")).thenReturn(user);
        assertThrows(RuntimeException.class, () -> userService.saveUser(user));
    }

    @Test
    void getUser_IfExistsInDatabase() {
        String email = "roker@gmail.com";
        AppUser user = new AppUser("Roker", "Rokerovich", "roker@gmail.com", "123456");

        when(appUserRepository.findByEmail(email)).thenReturn(user);

        AppUser newUser = userService.getUser(email);
        assertEquals(email, newUser.getEmail());
    }

    @Test
    void getUser_IfDoesNotExistsInDatabase() {
        String email = "roker@gmail.com";

        when(appUserRepository.findByEmail(email)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.getUser(email));
    }

    @Test
    void getUserById_IfDoesNotExistsInDatabase() {
        Optional<AppUser> userOptional = Optional.empty();

        when(appUserRepository.findById(1L)).thenReturn(userOptional);
        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUserById_IfExistsInDatabase() {
        AppUser user = new AppUser(1L, "Roker", "Rokerovich", "roker@gmail.com", "123456", null, null, null);
        Optional<AppUser> userOptional = Optional.of(user);
        AppUser updatedUser = new AppUser(1L, "NewName", "NewLastName", "NewEmail", "123456", null, null, null);

        when(appUserRepository.findById(1L)).thenReturn(userOptional);
        when(appUserRepository.save(updatedUser)).thenReturn(updatedUser);

        AppUser newUser = userService.updateUserById(1L, "NewName", "NewLastName", "NewEmail");
        assertEquals(user.getId(), newUser.getId());
        assertEquals(user.getPassword(), newUser.getPassword());
        assertNotEquals("Roker", newUser.getName());
        assertNotEquals("Rokerovich", newUser.getLastName());
        assertNotEquals("roker@gmail.com", newUser.getEmail());
    }

    @Test
    void updateUserById_IfDoesNotExistsInDatabase() {
        Optional<AppUser> userOptional = Optional.empty();

        when(appUserRepository.findById(1L)).thenReturn(userOptional);
        assertThrows(RuntimeException.class, () ->
                userService.updateUserById(1L, "NewName", "NewLastName", "NewEmail"));
    }

    @Test
    void deleteUserById_IfExistsInDatabase() {
        AppUser user = new AppUser(1L, "Roker", "Rokerovich", "roker@gmail.com", "123456", null, null, null);
        Optional<AppUser> userOptional = Optional.of(user);

        when(appUserRepository.findById(1L)).thenReturn(userOptional);

        userService.deleteUserById(1L);
        verify(appUserRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_IfDoesNotExistsInDatabase() {
        Optional<AppUser> userOptional = Optional.empty();

        when(appUserRepository.findById(1L)).thenReturn(userOptional);
        assertThrows(RuntimeException.class, () ->
                userService.deleteUserById(1L));
    }

    @Test
    void userShouldExistsAndHaveRoleAdmin() {
        List<Role> roles = new ArrayList<>();
        Role role = new Role(1L, "ROLE_ADMIN");
        roles.add(role);
        AppUser user = new AppUser(1L, "a", "a", "a", "a", roles, null, "a");

        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(user);
        assertTrue(userService.isAdmin(user.getEmail()));
    }

    @Test
    void userShouldExistsAndDoesNotHaveRoleAdmin() {
        List<Role> roles = new ArrayList<>();
        Role role = new Role(1L, "ROLE_USER");
        roles.add(role);
        AppUser user = new AppUser(1L, "a", "a", "a", "a", roles, null, "a");

        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(user);
        assertFalse(userService.isAdmin(user.getEmail()));
    }

    @Test
    void userShouldNotExistsAndHaveRoleAdmin() {
        String email = "email";

        when(appUserRepository.findByEmail(email)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> userService.isAdmin(email));
    }

    @Test
    void createUser_IfDoesNotExistsInDatabase() {
        CreateNewUserForm form = new CreateNewUserForm("NewName", "NewSurname", "NewEmail", "NewPassword", "Organization");
        AppUser user = new AppUser("NewName", "NewSurname", "NewEmail", "NewPassword");
        user.setOrganizationName("Organization");
        Role role = new Role(1L, "ROLE_USER");
        user.getRoles().add(role);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);
        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("NewPassword");
        when(appUserRepository.save(user)).thenReturn(user);

        AppUser newUser = userService.createUser(form);
        assertTrue(newUser.getRoles().contains(role));
    }

    @Test
    void createUser_IfAlreadyExistsInDatabase() {
        CreateNewUserForm form = new CreateNewUserForm("NewName", "NewSurname", "NewEmail", "NewPassword", "Organization");
        AppUser user = new AppUser("NewName", "NewSurname", "NewEmail", "NewPassword");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(new Role(1L, "ROLE_USER"));
        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userService.createUser(form));
    }

}