package com.adv.taskmanager.controller;

import com.adv.taskmanager.dtos.TaskDto;
import com.adv.taskmanager.model.Task;
import com.adv.taskmanager.model.User;
import com.adv.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto, @AuthenticationPrincipal User user) {
        Task createdTask = taskService.createTask(taskDto, user.getId());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto, @AuthenticationPrincipal User user) {
        Task updatedTask = taskService.updateTask(id, taskDto, user.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/get")
    public ResponseEntity<List<TaskDto>> getAllTasks(@AuthenticationPrincipal User user) {
        List<TaskDto> tasks = taskService.getUserTasks(user.getId());
        return ResponseEntity.ok(tasks);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsCompleted(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Task completedTask = taskService.markTaskAsCompleted(id, user.getId());
        return ResponseEntity.ok(completedTask);
    }

    @GetMapping("/completed-count")
    public Long getCompletedTasks(
            @RequestParam Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return taskService.getCompletedTasksCount(userId, start, end);
    }

    @GetMapping("/completion-metrics")
    public Map<String, Long> getCompletionMetrics(
            @RequestParam Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return taskService.getTaskCompletionMetrics(userId, start, end);
    }
}
