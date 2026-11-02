package com.taskapp.domain;

import com.taskapp.domain.entity.TaskPriority;
import java.time.LocalDate;

public record CreateTaskRequest(
        String title, String description, LocalDate dueDate, TaskPriority priority) {
}
