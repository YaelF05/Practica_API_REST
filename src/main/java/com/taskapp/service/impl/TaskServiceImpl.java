package com.taskapp.service.impl;

import com.taskapp.domain.CreateTaskRequest;
import com.taskapp.domain.entity.Task;
import com.taskapp.domain.entity.TaskStatus;
import com.taskapp.repository.TaskRepository;
import com.taskapp.service.TaskService;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(CreateTaskRequest request) {
        Instant now = Instant.now();
        Task task = new Task(request.title(), request.description(), request.dueDate(),
                TaskStatus.OPEN, request.priority(), now, now);
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> listTasks() {
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, "created"));
    }
}
