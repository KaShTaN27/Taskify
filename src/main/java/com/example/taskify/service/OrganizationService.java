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
import java.util.Optional;

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
        if (organizationRepository.findByName(organization.getName()) == null) {
            log.info("Saving new organization {} to the database", organization.getName());
            return organizationRepository.save(organization);
        } else {
            log.error("Organization {} already exists in database", organization.getName());
            return organization;
        }
    }

    public Organization getOrganization(String name) {
        Organization organization = organizationRepository.findByName(name);
        if (organization != null) {
            log.info("Organization {} found in database", name);
            return organization;
        } else {
            log.error("There is no such organization {}", name);
            return new Organization();
        }
    }

    public Organization updateOrganizationById(Long id, String name,
                                               String address, String phoneNumber) {
        Optional<Organization> optionalOrganization = organizationRepository.findById(id);
        if (optionalOrganization.isPresent()) {
            Organization organization = optionalOrganization.get();
            organization.setName(name);
            organization.setAddress(address);
            organization.setPhoneNumber(phoneNumber);
            log.info("Organization with id {} updated successfully", id);
            return organizationRepository.save(organization);
        } else {
            log.error("There is no such organization with id {}", id);
            return new Organization();
        }
    }

    public void deleteOrganization(Long id) {
        if (organizationRepository.findById(id).isPresent()) {
            log.info("Organization with id {} deleted successfully", id);
            organizationRepository.deleteById(id);
        } else {
            log.error("There is no such organization with id {}", id);
        }
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
