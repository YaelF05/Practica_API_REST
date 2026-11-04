package com.taskapp.service;

import com.taskapp.domain.CreateTaskRequest;
import com.taskapp.domain.entity.Task;
import java.util.List;
import java.util.UUID;
import com.taskapp.domain.UpdateTaskRequest;

public interface TaskService {
    Task createTask(CreateTaskRequest request);
    List<Task> listTasks();
    Task updateTask(UUID taskId, UpdateTaskRequest request);
    void deleteTask(UUID taskId);
}
