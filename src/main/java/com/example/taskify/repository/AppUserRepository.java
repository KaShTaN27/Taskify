package com.example.taskify.repository;

import com.example.taskify.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
    List<AppUser> findAllByOrganizationName(String name);
}
