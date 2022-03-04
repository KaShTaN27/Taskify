package com.example.taskify.service;

import com.example.taskify.controller.form.AssignTaskForm;
import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Task;
import com.example.taskify.email.EmailSenderService;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final EmailSenderService senderService;

    private final TaskRepository taskRepository;
    private final AppUserRepository appUserRepository;

    public Task saveTask(Task task) {
        if (taskRepository.findByTitle(task.getTitle()) == null) {
            log.info("Saving new task {} to the database", task.getTitle());
            return taskRepository.save(task);
        } else {
            log.error("Task with title {} already exists in database", task.getTitle());
            throw new RuntimeException("Task already exists in database");
        }
    }

    public Task getTask(String title) {
        Task task = taskRepository.findByTitle(title);
        if (task != null) {
            log.info("Fetching task with title: {}", title);
            return task;
        } else {
            log.error("There is no such task with title: {}", title);
            throw new RuntimeException("There is no such task in database");
        }
    }

    public Task getTaskById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        return optionalTask.orElseThrow(() -> new RuntimeException("There is no task with such id"));
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
            throw new RuntimeException("There is no such task in database");
        }
    }

    public void deleteTaskById(Long id) {
        if (taskRepository.findById(id).isPresent()) {
            log.info("Task with id {} deleted successfully", id);
            taskRepository.deleteById(id);
        } else {
            log.error("There is no such task with id {} in database", id);
            throw new RuntimeException("There is no such task in database");
        }
    }

    public void addTaskToUsers(List<String> emails, String title) {
        Task task = taskRepository.findByTitle(title);
        emails.forEach( email -> {
            AppUser user = appUserRepository.findByEmail(email);
            if (user != null)
                user.getTasks().add(task);
        });
    }

    public void createTaskAndSendEmail(AssignTaskForm form) {
        saveTask(new Task(form.getTitle(), form.getDescription(), form.getDeadline(), form.getIsDone()));
        addTaskToUsers(form.getEmails(), form.getTitle());
        form.getEmails().forEach(email -> senderService.sendSimpleEmail(email, form.getTitle(), form.getDescription(), form.getDeadline()));
    }
}
