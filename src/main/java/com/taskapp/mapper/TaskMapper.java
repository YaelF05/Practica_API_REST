package com.taskapp.mapper;

import com.taskapp.domain.CreateTaskRequest;
import com.taskapp.domain.dto.CreateTaskRequestDto;
import com.taskapp.domain.dto.TaskDto;
import com.taskapp.domain.entity.Task;
import org.springframework.stereotype.Component;
import com.taskapp.domain.UpdateTaskRequest;
import com.taskapp.domain.dto.UpdateTaskRequestDto;

@Component
public class TaskMapper {
    public CreateTaskRequest fromDto(CreateTaskRequestDto dto) {
        return new CreateTaskRequest(dto.title(), dto.description(), dto.dueDate(), dto.priority());
    }

    public TaskDto toDto(Task task) {
        return new TaskDto(task.getId(), task.getTitle(), task.getDescription(),
                task.getDueDate(), task.getPriority(), task.getStatus());
    }

    public UpdateTaskRequest fromDto(UpdateTaskRequestDto dto) {
        return new UpdateTaskRequest(dto.title(), dto.description(), dto.dueDate(),
                dto.status(), dto.priority());
    }
}
