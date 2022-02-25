package com.example.taskify.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String deadline;
    private boolean isDone;
    @ManyToMany
    @JoinTable(
            name = "app_user_tasks",
            joinColumns = @JoinColumn(name = "tasks_id"),
            inverseJoinColumns = @JoinColumn(name = "app_user_id")
    )
    @JsonIgnore
    private Collection<AppUser> users = new ArrayList<>();

    public Task(String title, String description, String deadline, boolean isDone) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isDone = isDone;
    }
}
