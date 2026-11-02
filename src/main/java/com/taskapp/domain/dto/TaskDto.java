package com.taskapp.domain.dto;

import com.taskapp.domain.entity.TaskPriority;
import com.taskapp.domain.entity.TaskStatus;
import java.time.LocalDate;
import java.util.UUID;

public record TaskDto(UUID id, String title, String description, LocalDate dueDate,
                      TaskPriority priority, TaskStatus status) {
}
