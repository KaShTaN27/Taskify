package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.domain.Role;
import com.example.taskify.domain.Task;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.OrganizationRepository;
import com.example.taskify.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository orgRepo;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private OrganizationService orgService;

    private static final Organization TEST_ORGANIZATION = new Organization(1L, "name", "123456", "Address", new ArrayList<>());

    @Test
    void saveOrganization_IfdoesNotExistsInDatabase() {

        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(null);
        when(orgRepo.save(TEST_ORGANIZATION)).thenReturn(TEST_ORGANIZATION);

        Organization newOrg = orgService.saveOrganization(TEST_ORGANIZATION);
        assertEquals(TEST_ORGANIZATION, newOrg);
    }

    @Test
    void saveOrganization_IfAlreadyExistsInDatabase() {
        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(TEST_ORGANIZATION);

        assertThrows(RuntimeException.class, () -> orgService.saveOrganization(TEST_ORGANIZATION));
    }

    @Test
    void getOrganization_IfExistsInDatabase() {

        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(TEST_ORGANIZATION);

        Organization newOrg = orgService.getOrganization(TEST_ORGANIZATION.getName());
        assertEquals(TEST_ORGANIZATION, newOrg);
    }

    @Test
    void getOrganization_IfDoesNotExistsInDatabase() {
        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> orgService.getOrganization(TEST_ORGANIZATION.getName()));
    }

    @Test
    void updateOrganizationById_IfExistsInDatabase() {
        Optional<Organization> optionalOrg = Optional.of(TEST_ORGANIZATION);
        Organization org = new Organization(1L, "newName", "654321", "newAddress", null);
        TEST_ORGANIZATION.setAppUsers(null);

        when(orgRepo.findById(TEST_ORGANIZATION.getId())).thenReturn(optionalOrg);
        when(orgRepo.save(org)).thenReturn(org);

        Organization newOrg = orgService.updateOrganizationById(TEST_ORGANIZATION.getId(), "newName", "newAddress", "654321");
        assertEquals(org.getId(), newOrg.getId());
        assertNotEquals("name", newOrg.getName());
        assertNotEquals("123456", newOrg.getPhoneNumber());
        assertNotEquals("Address", newOrg.getAddress());
    }

    @Test
    void updateOrganizationById_IfDoesNotExistsInDatabase() {
        Optional<Organization> optionalOrg =  Optional.empty();

        when(orgRepo.findById(TEST_ORGANIZATION.getId())).thenReturn(optionalOrg);
        assertThrows(RuntimeException.class, () ->
                orgService.updateOrganizationById(TEST_ORGANIZATION.getId(), "newName", "newAddress", "654321"));
    }

    @Test
    void deleteOrganization_IfExistsInDatabase() {
        Optional<Organization> optionalOrganization = Optional.of(TEST_ORGANIZATION);

        when(orgRepo.findById(TEST_ORGANIZATION.getId())).thenReturn(optionalOrganization);

        orgService.deleteOrganization(TEST_ORGANIZATION.getId());
        verify(orgRepo, times(1)).deleteById(TEST_ORGANIZATION.getId());
    }

    @Test
    void deleteOrganization_IfDoesNotExistsInDatabase() {
        Optional<Organization> optionalOrganization = Optional.empty();

        when(orgRepo.findById(TEST_ORGANIZATION.getId())).thenReturn(optionalOrganization);
        assertThrows(RuntimeException.class, () -> orgService.deleteOrganization(TEST_ORGANIZATION.getId()));
    }

    @Test
    void getOrganizationTasks_IfOrganizationUsersAndTasksExists() {
        Task task1 = new Task("title1", "description1", "deadline1", false);
        Task task2 = new Task("title2", "description2", "deadline2", false);
        Collection<Task> testTasks = new ArrayList<>();
        testTasks.add(task1);
        testTasks.add(task2);
        AppUser user = new AppUser(1L, "name", "lastName", "email", "password", null, Arrays.asList(task1, task2), TEST_ORGANIZATION.getName());
        TEST_ORGANIZATION.setAppUsers(List.of(user));

        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(TEST_ORGANIZATION);
        Collection<Task> tasks = orgService.getOrganizationTasks(user.getEmail());
        assertEquals(testTasks, tasks);
    }

    @Test
    void addUserToOrganization_IfOrganizationAndUserExists() {
        AppUser user = new AppUser(1L, "name", "lastName", "email", "password", null, null, TEST_ORGANIZATION.getName());
        TEST_ORGANIZATION.setAppUsers(new ArrayList<>());

        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(TEST_ORGANIZATION);
        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(user);
        orgService.addUserToOrganization(TEST_ORGANIZATION.getName(), user.getEmail());
        assertNotEquals(null, TEST_ORGANIZATION.getAppUsers());
    }

    @Test
    void ShouldGetAllOrganization_IfExists() {
        List<Organization> organizations = List.of(TEST_ORGANIZATION);

        when(orgRepo.findAll()).thenReturn(organizations);
        List<Organization>  newOrganizations = orgService.getOrganizations();
        assertNotEquals(null, newOrganizations);
    }

    // Добавление этого теста не влияет на покрытиие
    @Test
    void ShouldGetAllOrganization_IfDoesNotExists() {

        when(orgRepo.findAll()).thenReturn(null);
        List<Organization>  newOrganizations = orgService.getOrganizations();
        assertNull(newOrganizations);
    }


    @Test
    void addAdminToOrganization() {
        Role role = new Role(1L, "ROLE_ADMIN");
        AppUser user = new AppUser(1L, "name", "lastName", "email", "password", List.of(role), null, TEST_ORGANIZATION.getName());

        when(appUserRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(orgRepo.findByName(TEST_ORGANIZATION.getName())).thenReturn(TEST_ORGANIZATION);

        // Как сделать так, чтобы мы инициализировали userService и переходили к методу
        // addRoleToUser
        orgService.addAdminToOrganization(TEST_ORGANIZATION.getName(), user.getEmail());
        assertTrue(TEST_ORGANIZATION.getAppUsers().contains(user));
    }
}