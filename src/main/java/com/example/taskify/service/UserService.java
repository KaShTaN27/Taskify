package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.domain.Role;
import com.example.taskify.domain.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface UserService {
    void addAdminToOrganization(String organizationName, String userEmail);
    Organization saveOrganization(Organization organization);
    Role saveRole(Role role);
    AppUser saveUser(AppUser user);
    Task saveTask(Task task);
    void addTaskToUsers(ArrayList<String> emails, String title);
    void addUserToOrganization(String organizationName, String email);
    void addRoleToUser(String email, String roleName);
    List<AppUser> getUsers(String orgName);
    AppUser getUser(String email);
    Organization getOrganization(String name);
    List<Organization> getOrganizations();
}
