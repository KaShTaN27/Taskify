package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Task;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;

    public Task saveTask(Task task) {
        log.info("Saving new task {} to the database", task.getTitle());
        return taskRepository.save(task);
    }

    public void addTaskToUsers(ArrayList<String> emails, String title) {
        Task task = taskRepository.findByTitle(title);
        emails.forEach( email -> {
            AppUser user = appUserRepository.findByEmail(email);
            if (user != null)
                user.getTasks().add(task);
        });
    }
}
