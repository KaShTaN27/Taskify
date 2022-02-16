package com.example.taskify.repository;

import com.example.taskify.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Organization findByName(String name);
//  Optional<Organization> findByName(String name);
}
