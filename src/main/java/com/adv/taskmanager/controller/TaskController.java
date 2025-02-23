package com.adv.taskmanager.controller;

import com.adv.taskmanager.dtos.TaskComparisonDto;
import com.adv.taskmanager.dtos.TaskDto;
import com.adv.taskmanager.dtos.UserDto;
import com.adv.taskmanager.model.Task;
import com.adv.taskmanager.model.User;
import com.adv.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskDto taskDto, Authentication authentication) {
        TaskDto createdTask = taskService.createTask(taskDto, authentication);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<TaskDto> startTask(@PathVariable Long id, Authentication authentication) {
        TaskDto startedTask = taskService.startTask(id, authentication);
        return ResponseEntity.ok(startedTask);
    }

    @PutMapping("{id}/pause")
    public ResponseEntity<TaskDto> pauseTask(@PathVariable Long id, Authentication authentication) {
        TaskDto pausedTask = taskService.pauseTask(id, authentication);
        return ResponseEntity.ok(pausedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto, @AuthenticationPrincipal User user) {
        Task updatedTask = taskService.updateTask(id, taskDto, user.getId());
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/get")
    public ResponseEntity<List<TaskDto>> getAllTasks(Authentication authentication) {

        List<TaskDto> tasks = taskService.getUserTasks(authentication);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/get/today")
    public ResponseEntity<Map<String, List<TaskDto>>> getTodaysTasks(Authentication authentication) {

        Map<String, List<TaskDto>> result = taskService.getTodaysTasks(authentication);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/pending")
    public ResponseEntity<List<TaskDto>> getPendingTasks(Authentication authentication) {

        List<TaskDto> tasks = taskService.getPendingTasks(authentication);
        return ResponseEntity.ok(tasks);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        taskService.deleteTask(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id, Authentication authentication) {
        TaskDto task = taskService.getTask(id, authentication);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskDto> markTaskAsCompleted(@PathVariable Long id, Authentication authentication) {
        TaskDto completedTask = taskService.markTaskAsCompleted(id, authentication);
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

    @GetMapping("/time-spent-comparison")
    public ResponseEntity<TaskComparisonDto> getTimeSpentComparison(Authentication authentication) {
        TaskComparisonDto comparison = taskService.getTimeSpentComparison(authentication);
        return ResponseEntity.ok(comparison);
    }

    @GetMapping("/priority-completion")
    public ResponseEntity<HashMap> getPriorityWiseCompletion(Authentication authentication) {

        HashMap map = taskService.getPriorityWiseCompletion(authentication);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/on-time-rate")
    public ResponseEntity<Double> getOnTimeCompletionRate(Authentication authentication) {
        Double rate = taskService.getOnTimeCompletionRate(authentication);
        return ResponseEntity.ok(rate);
    }
}
