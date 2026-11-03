package com.taskapp.service;

import com.taskapp.domain.CreateTaskRequest;
import com.taskapp.domain.entity.Task;
import java.util.List;

public interface TaskService {
    Task createTask(CreateTaskRequest request);
    List<Task> listTasks();
}
