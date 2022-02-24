package com.example.taskify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    @ManyToMany
    private Collection<Role> roles = new ArrayList<>();
    @ManyToMany(fetch = LAZY)
    private Collection<Task> tasks = new ArrayList<>();
    private String organizationName;

    public AppUser(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }
}
