package com.taskapp.domain.dto;

import com.taskapp.domain.entity.TaskPriority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;

public record CreateTaskRequestDto(
        @NotBlank(message = "El titulo no puede estar vacio")
        @Length(max = 255, message = "El titulo debe tener como maximo 255 caracteres")
        String title,

        @Length(max = 1000, message = "La descripcion debe tener como maximo 1000 caracteres")
        String description,

        @FutureOrPresent(message = "La fecha debe ser hoy o una fecha futura")
        LocalDate dueDate,

        @NotNull(message = "La prioridad es obligatoria")
        TaskPriority priority) {
}
