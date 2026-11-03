package com.taskapp;

import com.taskapp.domain.entity.Task;
import com.taskapp.domain.entity.TaskPriority;
import com.taskapp.domain.entity.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UpdateTaskApiTest extends TaskApiTestSupport {
    @Test
    void replacesEditableFieldsAndPreservesIdAndCreationTime() throws Exception {
        Instant original = Instant.parse("2026-01-01T10:00:00Z");
        Task task = repository.saveAndFlush(new Task("Original", "Antes", LocalDate.now(),
                TaskStatus.OPEN, TaskPriority.HIGH, original, original));
        UUID id = task.getId();

        mvc.perform(put("/api/v1/tasks/{id}", id).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("title", "Actualizada", "status", "COMPLETE",
                        "priority", "LOW"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Actualizada"))
                .andExpect(jsonPath("$.status").value("COMPLETE"))
                .andExpect(jsonPath("$.priority").value("LOW"));
        repository.flush();
        entityManager.clear();
        Task saved = repository.findById(id).orElseThrow();
        assertThat(saved.getCreated()).isEqualTo(original);
        assertThat(saved.getUpdated()).isAfter(original);
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.COMPLETE);
        assertThat(saved.getDescription()).isNull();
        assertThat(saved.getDueDate()).isNull();
    }

    @Test
    void returnsStandard400WhenTaskDoesNotExist() throws Exception {
        mvc.perform(put("/api/v1/tasks/{id}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Tarea\",\"status\":\"COMPLETE\",\"priority\":\"LOW\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void returnsStandard400ForInvalidUuid() throws Exception {
        mvc.perform(put("/api/v1/tasks/no-es-uuid").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Tarea\",\"status\":\"OPEN\",\"priority\":\"LOW\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidUpdates")
    void rejectsInvalidUpdateWithoutChangingStoredTask(String request) throws Exception {
        String id = createTask("Original").get("id").asText();
        mvc.perform(put("/api/v1/tasks/{id}", id).contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
        assertThat(repository.findById(UUID.fromString(id)).orElseThrow().getTitle()).isEqualTo("Original");
    }

    static Stream<String> invalidUpdates() {
        return Stream.of(
                "{\"title\":\"Tarea\",\"priority\":\"LOW\"}",
                "{\"title\":\"Tarea\",\"status\":null,\"priority\":\"LOW\"}",
                "{\"title\":\"Tarea\",\"status\":\"DONE\",\"priority\":\"LOW\"}",
                "{\"title\":\" \",\"status\":\"OPEN\",\"priority\":\"LOW\"}",
                "{\"title\":\"Tarea\",\"status\":\"OPEN\"}",
                "{\"title\":\"Tarea\",\"status\":\"OPEN\",\"priority\":\"LOW\",\"dueDate\":\""
                        + LocalDate.now().minusDays(1) + "\"}");
    }
}
