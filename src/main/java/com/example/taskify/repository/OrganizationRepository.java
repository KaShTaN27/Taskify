package com.example.taskify.repository;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Organization findByName(String name);
}
