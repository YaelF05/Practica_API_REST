package com.taskapp.domain;

import com.taskapp.domain.entity.TaskPriority;
import com.taskapp.domain.entity.TaskStatus;
import java.time.LocalDate;

public record UpdateTaskRequest(String title, String description, LocalDate dueDate,
                                TaskStatus status, TaskPriority priority) {
}
