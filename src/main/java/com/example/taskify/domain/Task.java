package com.example.taskify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id @GeneratedValue(strategy = AUTO)
    private Long id;
    private String title;
    private String description;
    private String deadline;
    private boolean isDone;

    public Task(String title, String description, String deadline, boolean isDone) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.isDone = isDone;
    }
}
