package com.taskapp.controller;

import com.taskapp.domain.dto.CreateTaskRequestDto;
import com.taskapp.domain.dto.TaskDto;
import com.taskapp.mapper.TaskMapper;
import com.taskapp.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import com.taskapp.domain.dto.UpdateTaskRequestDto;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequestDto dto) {
        var task = taskService.createTask(taskMapper.fromDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toDto(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> listTasks() {
        var tasks = taskService.listTasks().stream().map(taskMapper::toDto).toList();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable UUID taskId,
                                             @Valid @RequestBody UpdateTaskRequestDto dto) {
        var task = taskService.updateTask(taskId, taskMapper.fromDto(dto));
        return ResponseEntity.ok(taskMapper.toDto(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
