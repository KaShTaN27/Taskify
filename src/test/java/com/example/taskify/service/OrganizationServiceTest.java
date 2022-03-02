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
    void deleteOrganization() {
    }
}