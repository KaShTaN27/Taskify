package com.example.taskify.service;

import com.example.taskify.domain.Organization;
import com.example.taskify.domain.Task;
import com.example.taskify.exception.ResourceAlreadyExistsException;
import com.example.taskify.exception.ResourceNotFoundException;
import com.example.taskify.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private static final String ADMIN = "ROLE_ADMIN";

    private final UserService userService;

    private final OrganizationRepository organizationRepository;

    public Organization saveOrganization(Organization organization) {
        if (organizationRepository.findByName(organization.getName()).isEmpty()) {
            log.info("Saving new organization {} to the database", organization.getName());
            return organizationRepository.save(organization);
        } else {
            log.error("Organization {} already exists in database", organization.getName());
            throw new ResourceAlreadyExistsException("Organization " + organization.getName() + " already exists in database");
        }
    }

    public Organization getOrganizationByName(String name) {
        return organizationRepository.findByName(name).orElseThrow(() ->
                new ResourceNotFoundException(name + "doesn't exists in database"));
    }

    public Organization getOrganizationById(Long id) {
        return organizationRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Organization with id = " + id + "doesn't exists in database"));
    }

    public Organization updateOrganizationById(Long id, String name,
                                               String address, String phoneNumber) {
        Organization organization = getOrganizationById(id);
        organization.setName(name);
        organization.setAddress(address);
        organization.setPhoneNumber(phoneNumber);
        log.info("Organization with id {} updated successfully", id);
        return organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        if (organizationRepository.findById(id).isPresent()) {
            log.info("Organization with id {} deleted successfully", id);
            organizationRepository.deleteById(id);
        } else {
            log.error("There is no such organization with id {}", id);
            throw new ResourceNotFoundException("Organization with id = " + id + "doesn't exists in database");
        }
    }

    public Collection<Task> getOrganizationTasks(String memberEmail) {
        Collection<Task> tasks = new ArrayList<>();
        Collection<Task> uniqueTasks = new ArrayList<>();
        Organization organization = getOrganizationByName(userService.getUserByEmail(memberEmail).getOrganizationName());
        organization.getAppUsers().forEach(member -> tasks.addAll(member.getTasks()));
        tasks.forEach(task -> {
            if (!uniqueTasks.contains(task))
                uniqueTasks.add(task);
        });
        return uniqueTasks;
    }

    public List<Organization> getOrganizations() {
        log.info("Fetching all organizations");
        return organizationRepository.findAll();
    }

    public void addUserToOrganization(String organizationName, String email) {
        log.info("Adding user with email {} to {}", email, organizationName);
        getOrganizationByName(organizationName).getAppUsers().add(userService.getUserByEmail(email));
    }

    public void addAdminToOrganization(String organizationName, String userEmail) {
        userService.addRoleToUser(userEmail, ADMIN);
        addUserToOrganization(organizationName, userEmail);
        log.info("Created organization {} with admin {}", organizationName, userEmail);
    }
}
