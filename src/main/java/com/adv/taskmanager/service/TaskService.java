package com.adv.taskmanager.service;

import com.adv.taskmanager.Repositories.TaskRepo;
import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.dtos.TaskDto;
import com.adv.taskmanager.model.Priority;
import com.adv.taskmanager.model.Task;
import com.adv.taskmanager.model.TaskStatus;
import com.adv.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {


    private final TaskRepo taskRepo;
    private final UserRepo userRepo;

    @Autowired
    public TaskService(TaskRepo taskRepo, UserRepo userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public Task createTask(TaskDto taskDto, Long userId) {

        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDateTime(taskDto.getDueDateTime());
        task.setPriority(Priority.valueOf(taskDto.getPriority()));
        task.setStatus(taskDto.getStatus() != null ? TaskStatus.valueOf(taskDto.getStatus()) : TaskStatus.PENDING);
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());

        return taskRepo.save(task);
    }

    public List<TaskDto> getUserTasks(Long userId) {
        List<Task> tasks = taskRepo.findByUserId(userId);
        return tasks.stream().map(TaskDto::new).collect(Collectors.toList());
    }

    public Task updateTask(Long taskId, TaskDto taskDto, Long userId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to update this task");
        }

        if (taskDto.getTitle() != null) task.setTitle(taskDto.getTitle());
        if (taskDto.getDescription() != null) task.setDescription(taskDto.getDescription());
        if (taskDto.getDueDateTime() != null) task.setDueDateTime(taskDto.getDueDateTime());
        if (taskDto.getPriority() != null) task.setPriority(Priority.valueOf(taskDto.getPriority()));
        if (taskDto.getStatus() != null) task.setStatus(TaskStatus.valueOf(taskDto.getStatus()));

        return taskRepo.save(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this task");
        }

        taskRepo.delete(task);
    }

    public Task markTaskAsCompleted(Long taskId, Long userId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to update this task");
        }

        task.setStatus(TaskStatus.COMPLETED);
        return taskRepo.save(task);
    }

    public Task startTask(Long taskId, Long userId) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to start this task");
        }

        if (task.getStatus() != TaskStatus.PENDING) {
            throw new IllegalStateException("Task can only be started if it's in PENDING state");
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        return taskRepo.save(task);
    }




}
