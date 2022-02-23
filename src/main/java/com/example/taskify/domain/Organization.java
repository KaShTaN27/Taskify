package com.example.taskify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
    @OneToMany
    private Collection<AppUser> appUsers = new ArrayList<>();

    public Organization(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.appUsers = new ArrayList<>();
    }
}
