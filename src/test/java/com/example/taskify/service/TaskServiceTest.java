package com.example.taskify.service;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Task;
import com.example.taskify.exception.ResourceAlreadyExistsException;
import com.example.taskify.exception.ResourceNotFoundException;
import com.example.taskify.repository.AppUserRepository;
import com.example.taskify.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private TaskService taskService;

    private static final Task TEST_TASK = new Task(1L, "title", "description", "99-99-9999", false, new ArrayList<>());

    private static final AppUser TEST_USER = new AppUser(1L, "name", "lastName", "email",
            "password", new ArrayList<>(), new ArrayList<>(), "organization");

    @Test
    void saveTask_IfDoesNotExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(Optional.empty());
        when(taskRepository.save(TEST_TASK)).thenReturn(TEST_TASK);

        Task task = taskService.saveTask(TEST_TASK);
        assertEquals(task, TEST_TASK);
    }

    @Test
    void saveTask_IfAlreadyExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(Optional.of(TEST_TASK));

        assertThrows(ResourceAlreadyExistsException.class, () -> taskService.saveTask(TEST_TASK));
    }

    @Test
    void getTask_IfExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(Optional.of(TEST_TASK));

        Task task = taskService.getTaskByTitle(TEST_TASK.getTitle());
        assertEquals(TEST_TASK, task);
    }

    @Test
    void getTask_IfDoesNotExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskByTitle(TEST_TASK.getTitle()));
    }

    @Test
    void getTaskById_IfExistsInDatabase() {
        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(Optional.of(TEST_TASK));

        Task task = taskService.getTaskById(TEST_TASK.getId());
        assertEquals(TEST_TASK, task);
    }

    @Test
    void getTaskById_IfDoesNotExistsInDatabase() {
        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(TEST_TASK.getId()));
    }

    @Test
    void updateTaskById_IfExistsInDatabase() {
        Task task = new Task(1L, "newTitle", "newDescription", "newDeadline", false, new ArrayList<>());

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(Optional.of(TEST_TASK));
        when(taskRepository.save(task)).thenReturn(task);

        Task newTask = taskService.updateTaskById(TEST_TASK.getId(), task.getTitle(), task.getDescription(), task.getDeadline());
        assertEquals(task.getId(), newTask.getId());
        assertNotEquals("title", newTask.getTitle());
        assertNotEquals("description", newTask.getDescription());
        assertNotEquals("99-99-9999", newTask.getDeadline());
    }

    @Test
    void updateTaskById_IfUserDoesNotExistsInDatabase() {
        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.updateTaskById(TEST_TASK.getId(), TEST_TASK.getTitle(), TEST_TASK.getDescription(), TEST_TASK.getDeadline()));
    }

    @Test
    void deleteTaskById_IfExistsInDatabase() {
        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(Optional.of(TEST_TASK));

        taskService.deleteTaskById(TEST_TASK.getId());
        verify(taskRepository, times(1)).deleteById(TEST_TASK.getId());
    }

    @Test
    void deleteTaskById_IfDoesNotExistsInDatabase() {
        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTaskById(TEST_TASK.getId()));
    }

    @Test
    void addTaskToUsers_IfUsersAndTaskExists() {
        List<String> emails = List.of(TEST_USER.getEmail());

        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(Optional.of(TEST_TASK));
        when(appUserRepository.findByEmail(TEST_USER.getEmail())).thenReturn(Optional.of(TEST_USER));

        taskService.addTaskToUsers(emails, TEST_TASK.getTitle());
        assertTrue(TEST_USER.getTasks().contains(TEST_TASK));
    }
}