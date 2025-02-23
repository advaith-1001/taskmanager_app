package com.adv.taskmanager.service;

import com.adv.taskmanager.Repositories.TaskRepo;
import com.adv.taskmanager.Repositories.UserRepo;
import com.adv.taskmanager.dtos.TaskComparisonDto;
import com.adv.taskmanager.dtos.TaskDto;
import com.adv.taskmanager.dtos.UserDto;
import com.adv.taskmanager.model.Priority;
import com.adv.taskmanager.model.Task;
import com.adv.taskmanager.model.TaskStatus;
import com.adv.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.adv.taskmanager.model.TaskStatus.*;

@Service
public class TaskService {


    private final TaskRepo taskRepo;
    private final UserRepo userRepo;

    @Autowired
    public TaskService(TaskRepo taskRepo, UserRepo userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public TaskDto createTask(TaskDto taskDto, Authentication authentication) {


        if (authentication == null || !authentication.isAuthenticated()) {
            return new TaskDto();
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDateTime(taskDto.getDueDateTime());
        task.setPriority(Priority.valueOf(taskDto.getPriority()));
        task.setStatus(taskDto.getStatus() != null ? TaskStatus.valueOf(taskDto.getStatus()) : PENDING);
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());

        taskRepo.save(task);

        TaskDto createdTask = new TaskDto(task);
        return createdTask;
    }

    public List<TaskDto> getUserTasks(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList();
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

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

    public void deleteTask(Long taskId, Authentication authentication) {


        if (authentication == null || !authentication.isAuthenticated()) {
            return; // ✅ Correct return type
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to delete this task");
        }

        taskRepo.delete(task);
    }

    public TaskDto markTaskAsCompleted(Long taskId, Authentication authentication) {


        if (authentication == null || !authentication.isAuthenticated()) {
            return new TaskDto();
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to update this task");
        }

        if (task.getStartedAt() != null) {
            Duration duration = Duration.between(task.getStartedAt(), LocalDateTime.now());
            long secondsSpent = duration.getSeconds();

            task.setTimeSpent((task.getTimeSpent() == null ? 0 : task.getTimeSpent()) + secondsSpent);
            task.setStartedAt(null);
        }
        task.setStatus(COMPLETED);
        taskRepo.save(task);

        TaskDto taskDto = new TaskDto(task);


        return taskDto;
    }

    public TaskDto startTask(Long taskId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return new TaskDto();
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized to update this task");
        }


        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            task.setStartedAt(LocalDateTime.now());
            task.setStatus(TaskStatus.IN_PROGRESS);
            taskRepo.save(task);
        }

        return new TaskDto(task);
    }

    public Long getCompletedTasksCount(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepo.countCompletedTasks(userId, startDate, endDate);
    }

    public Map<String, Long> getTaskCompletionMetrics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        Long completedOnTime = taskRepo.countOnTimeCompletedTasks(userId, startDate, endDate);
        Long completedLate = taskRepo.countLateCompletedTasks(userId, startDate, endDate);
        Long totalCompleted = completedOnTime + completedLate;

        Map<String, Long> metrics = new HashMap<>();
        metrics.put("completedOnTime", completedOnTime);
        metrics.put("completedLate", completedLate);
        metrics.put("totalCompleted", totalCompleted);

        return metrics;
    }


    public Map<String, List<TaskDto>> getTodaysTasks(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.singletonMap("Unauthorized", Collections.emptyList()); // ✅ Correct return type
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        List<Task> tasks = taskRepo.findByUserId(userId);

        LocalDate today = LocalDate.now();

        List<TaskDto> pendingTasks = tasks.stream()
                .filter(task -> (PENDING.equals(task.getStatus()) || IN_PROGRESS.equals(task.getStatus())) && task.getDueDateTime().toLocalDate().isEqual(today))
                .map(TaskDto::new)
                .collect(Collectors.toList());

        List<TaskDto> completedTasks = tasks.stream()
                .filter(task -> COMPLETED.equals(task.getStatus()) && task.getCompletedAt().toLocalDate().isEqual(today))
                .map(TaskDto::new)
                .collect(Collectors.toList());

        // Return both lists in a single response
        Map<String, List<TaskDto>> result = new HashMap<>();
        result.put("pendingTasks", pendingTasks);
        result.put("completedTasks", completedTasks);

        return result;

    }

    public List<TaskDto> getPendingTasks(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Collections.emptyList(); // ✅ Correct return type
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        List<Task> tasks = taskRepo.findByUserId(userId);

        List<TaskDto> pendingTasks = tasks.stream()
                .filter(task -> PENDING.equals(task.getStatus()) || IN_PROGRESS.equals(task.getStatus()))
                .map(TaskDto::new)
                .collect(Collectors.toList());

        return pendingTasks;

    }

    public TaskDto getTask(Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated!");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to view this task!");
        }

        long currentTimeSpent = task.getTimeSpent() != null ? task.getTimeSpent() : 0L;

        if (task.getStatus() == TaskStatus.IN_PROGRESS && task.getStartedAt() != null) {
            long additionalTime = Duration.between(task.getStartedAt(), LocalDateTime.now()).getSeconds();
            currentTimeSpent += additionalTime;
        }

        return new TaskDto(task);
    }

    public TaskDto pauseTask(Long id, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated!");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        if (!task.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to view this task!");
        }

        if (task.getStartedAt() != null) {
            Duration duration = Duration.between(task.getStartedAt(), LocalDateTime.now());
            long secondsSpent = duration.getSeconds();

            task.setTimeSpent((task.getTimeSpent() == null ? 0 : task.getTimeSpent()) + secondsSpent);
            task.setStartedAt(null);
            task.setStatus(TaskStatus.PENDING);
            taskRepo.save(task);
        }
        return new TaskDto(task);
    }

    public TaskComparisonDto getTimeSpentComparison(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated!");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        LocalDateTime startOfLastYear = LocalDateTime.now().minusYears(1).withDayOfYear(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfLastYear = LocalDateTime.now().minusYears(1).withMonth(12).withDayOfMonth(31).toLocalDate().atTime(23, 59, 59);

        LocalDateTime startOfThisYear = LocalDateTime.now().withDayOfYear(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfThisYear = LocalDateTime.now();

        List<Object[]> lastYearData = taskRepo.getTimeSpentPerYearRange(userId, startOfLastYear, endOfLastYear);
        List<Object[]> thisYearData = taskRepo.getTimeSpentPerYearRange(userId, startOfThisYear, endOfThisYear);

        Map<Integer, Long> lastYearMap = lastYearData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[1]).intValue(),  // month (1-12)
                        row -> ((Number) row[2]).longValue()  // timeSpent
                ));

        Map<Integer, Long> thisYearMap = thisYearData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[1]).intValue(),
                        row -> ((Number) row[2]).longValue()
                ));

        // Fill months 1-12 for both arrays
        List<Long> lastYear = new ArrayList<>();
        List<Long> thisYear = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            lastYear.add(lastYearMap.getOrDefault(month, 0L) / 3600);
            thisYear.add(thisYearMap.getOrDefault(month, 0L) / 3600);
        }

        return new TaskComparisonDto(
                lastYear,
                thisYear,
                List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        );
    }

    public HashMap getPriorityWiseCompletion(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated!");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        List<Task> tasks = taskRepo.findByUserId(userId);

        HashMap<Priority, Integer> map = new HashMap<>();
        map.put(Priority.LOW, 0);
        map.put(Priority.MEDIUM, 0);
        map.put(Priority.HIGH, 0);

        for (Task task : tasks) {
            Priority priority = task.getPriority();
            map.put(priority, map.get(priority) + 1);
        }

        return map;

    }

    public Double getOnTimeCompletionRate(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated!");
        }

        String email = authentication.getName();
        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();

        List<Task> tasks = taskRepo.findByUserId(userId);

        int total = tasks.size();

        int onTime = 0;

        for (Task task : tasks) {
            if(task.getCompletedAt().isBefore(task.getDueDateTime())) {
                onTime++;
            }
        }

        return ((double) onTime / total) * 100;


    }


}
