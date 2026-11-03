package com.taskapp.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID taskId) {
        super("No existe la tarea con id " + taskId);
    }
}
