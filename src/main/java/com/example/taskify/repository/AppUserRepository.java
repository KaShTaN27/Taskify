package com.example.taskify.repository;

import com.example.taskify.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findAllByOrganizationName(String name);
    boolean existsByEmail(String email);
}
