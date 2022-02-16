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
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;

    public Task saveTask(Task task) {
        if (taskRepository.findByTitle(task.getTitle()) == null) {
            log.info("Saving new task {} to the database", task.getTitle());
            return taskRepository.save(task);
        } else {
            log.error("Task with title {} already exists in database", task.getTitle());
            return task;
        }
    }

    public Task getTask(String title) {
        Task task = taskRepository.findByTitle(title);
        if (task != null) {
            log.info("Fetching task with title: {}", title);
            return task;
        } else {
            log.error("There is no such task with title: {}", title);
            return new Task();
        }
    }

    public Task updateTaskById(Long id, String title, String description, String deadline) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(title);
            task.setDescription(description);
            task.setDeadline(deadline);
            log.info("Task with id {} successfully updated", id);
            return taskRepository.save(task);
        } else {
            log.error("There is no such task with id {} in database", id);
            return new Task();
        }
    }

    public void deleteTask(Long id) {
        if (taskRepository.findById(id).isPresent()) {
            log.info("Task with id {} deleted successfully", id);
            taskRepository.deleteById(id);
        } else {
            log.error("There is no such task with id {} in database", id);
        }
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
