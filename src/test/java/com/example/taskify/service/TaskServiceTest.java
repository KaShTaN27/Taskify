package com.example.taskify.service;

import com.example.taskify.domain.Task;
import com.example.taskify.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private static final Task TEST_TASK = new Task(1L, "title", "description", "99-99-9999", false, new ArrayList<>());

    @Test
    void saveTask_IfDoesNotExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(null);
        when(taskRepository.save(TEST_TASK)).thenReturn(TEST_TASK);

        Task task = taskService.saveTask(TEST_TASK);
        assertEquals(task, TEST_TASK);
    }

    @Test
    void saveTask_IfAlreadyExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(TEST_TASK);

        assertThrows(RuntimeException.class, () -> taskService.saveTask(TEST_TASK));
    }

    @Test
    void getTask_IfExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(TEST_TASK);

        Task task = taskService.getTask(TEST_TASK.getTitle());
        assertEquals(TEST_TASK, task);
    }

    @Test
    void getTask_IfDoesNotExistsInDatabase() {
        when(taskRepository.findByTitle(TEST_TASK.getTitle())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> taskService.getTask(TEST_TASK.getTitle()));
    }

    @Test
    void getTaskById_IfExistsInDatabase() {
        Optional<Task> optionalTask = Optional.of(TEST_TASK);

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(optionalTask);
        Task task = taskService.getTaskById(TEST_TASK.getId());
        assertEquals(TEST_TASK, task);
    }

    @Test
    void getTaskById_IfDoesNotExistsInDatabase() {
        Optional<Task> optionalTask = Optional.empty();

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(optionalTask);
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(TEST_TASK.getId()));
    }

    @Test
    void updateTaskById_IfExistsInDatabase() {
        Optional<Task> optionalTask = Optional.of(TEST_TASK);
        Task task = new Task(1L, "newTitle", "newDescription", "newDeadline", false, new ArrayList<>());

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(optionalTask);
        when(taskRepository.save(task)).thenReturn(task);

        Task newTask = taskService.updateTaskById(TEST_TASK.getId(), task.getTitle(), task.getDescription(), task.getDeadline());
        assertEquals(task.getId(), newTask.getId());
        assertNotEquals("title", newTask.getTitle());
        assertNotEquals("description", newTask.getDescription());
        assertNotEquals("99-99-9999", newTask.getDeadline());
    }

    @Test
    void updateTaskById_IfUserDoesNotExistsInDatabase() {
        Optional<Task> optionalTask = Optional.empty();

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(optionalTask);
        assertThrows(RuntimeException.class, () ->
                taskService.updateTaskById(TEST_TASK.getId(), TEST_TASK.getTitle(), TEST_TASK.getDescription(), TEST_TASK.getDeadline()));
    }

    @Test
    void deleteTaskById_IfExistsInDatabase() {
        Optional<Task> optionalTask = Optional.of(TEST_TASK);

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(optionalTask);

        taskService.deleteTaskById(TEST_TASK.getId());
        verify(taskRepository, times(1)).deleteById(TEST_TASK.getId());
    }

    @Test
    void deleteTaskById_IfDoesNotExistsInDatabase() {
        Optional<Task> optionalTask = Optional.empty();

        when(taskRepository.findById(TEST_TASK.getId())).thenReturn(optionalTask);
        assertThrows(RuntimeException.class, () -> taskService.deleteTaskById(TEST_TASK.getId()));
    }

    @Test
    void addTaskToUsers() {
    }

    @Test
    void createTaskAndSendEmail() {
    }
}