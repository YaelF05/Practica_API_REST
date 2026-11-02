package com.taskapp.service;

import com.taskapp.domain.CreateTaskRequest;
import com.taskapp.domain.entity.Task;

public interface TaskService {
    Task createTask(CreateTaskRequest request);
}
