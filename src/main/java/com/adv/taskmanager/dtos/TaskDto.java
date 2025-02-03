package com.adv.taskmanager.dtos;

import com.adv.taskmanager.model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDto {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime dueDateTime;
    private String status;
    private String priority;
    private Long timeSpent;
    private Long categoryId;

    public TaskDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.createdAt = task.getCreatedAt();
        this.dueDateTime = task.getDueDateTime();
        this.timeSpent = task.getTimeSpent();
        this.status = String.valueOf(task.getStatus());
        this.priority = String.valueOf(task.getPriority());
        this.categoryId = (task.getCategory() != null) ? task.getCategory().getId() : null;
    }
}
