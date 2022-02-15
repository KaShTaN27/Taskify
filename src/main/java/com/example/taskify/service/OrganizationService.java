package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private static final String ADMIN = "ROLE_ADMIN";
    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final UserService userService;

    public Organization saveOrganization(Organization organization) {
        log.info("Saving new organization {} to the database", organization.getName());
        return organizationRepository.save(organization);
    }

    public Organization getOrganization(String name) {
        log.info("Organization {} found in database", name);
        return organizationRepository.findByName(name);
    }

    public List<Organization> getOrganizations() {
        log.info("Fetching all organizations");
        return organizationRepository.findAll();
    }

    public void addUserToOrganization(String organizationName, String email) {
        Organization organization = organizationRepository.findByName(organizationName);
        AppUser user = appUserRepository.findByEmail(email);
        log.info("Adding user with email {} to {}", email, organizationName);
        organization.getAppUsers().add(user);
    }

    public void addAdminToOrganization(String organizationName, String userEmail) {
        userService.addRoleToUser(userEmail, ADMIN);
        addUserToOrganization(organizationName, userEmail);
        log.info("Created organization {} with admin {}", organizationName, userEmail);
    }
}
