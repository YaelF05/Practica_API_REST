package com.taskapp;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateTaskApiTest extends TaskApiTestSupport {
    @Test
    void createsOpenTaskWithServerAuditFields() throws Exception {
        var response = createTask("Estudiar Spring");
        UUID id = UUID.fromString(response.get("id").asText());
        assertThat(response.get("status").asText()).isEqualTo("OPEN");
        assertThat(response.has("created")).isFalse();
        assertThat(response.has("updated")).isFalse();
        repository.flush();
        entityManager.clear();
        var saved = repository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("Estudiar Spring");
        assertThat(saved.getCreated()).isNotNull();
        assertThat(saved.getUpdated()).isEqualTo(saved.getCreated());
        assertThat(saved.getDescription()).isNull();
        assertThat(saved.getDueDate()).isNull();
    }

    @Test
    void acceptsLengthLimitsAndTodaysDate() throws Exception {
        mvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("title", "a".repeat(255),
                        "description", "b".repeat(1000), "dueDate", LocalDate.now().toString(),
                        "priority", "MEDIUM"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().toString()));
    }

    @Test
    void acceptsDateSerializedByProvidedUi() throws Exception {
        String day = LocalDate.now().plusDays(3).toString();
        mvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("title", "Desde UI", "priority", "LOW",
                        "dueDate", day + "T06:00:00.000Z", "status", "OPEN"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dueDate").value(day));
    }

    @Test
    void assignsIdStatusAndAuditOnServerEvenIfClientSendsThem() throws Exception {
        UUID suppliedId = UUID.randomUUID();
        String response = mvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("title", "Desde cliente", "priority", "HIGH",
                        "id", suppliedId.toString(), "status", "COMPLETE",
                        "created", "2000-01-01T00:00:00Z", "updated", "2000-01-01T00:00:00Z"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();
        UUID generatedId = UUID.fromString(json.readTree(response).get("id").asText());
        assertThat(generatedId).isNotEqualTo(suppliedId);
        assertThat(repository.findById(generatedId).orElseThrow().getCreated().getEpochSecond())
                .isGreaterThan(946684800L);
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void rejectsInvalidDataWithStandardError(String request) throws Exception {
        long before = repository.count();
        mvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
        assertThat(repository.count()).isEqualTo(before);
    }

    static Stream<String> invalidRequests() {
        return Stream.of(
                "{}",
                "{\"title\":\"   \",\"priority\":\"HIGH\"}",
                "{\"title\":null,\"priority\":\"HIGH\"}",
                "{\"title\":\"" + "a".repeat(256) + "\",\"priority\":\"HIGH\"}",
                "{\"title\":\"Tarea\",\"description\":\"" + "a".repeat(1001) + "\",\"priority\":\"HIGH\"}",
                "{\"title\":\"Tarea\",\"dueDate\":\"" + LocalDate.now().minusDays(1) + "\",\"priority\":\"HIGH\"}",
                "{\"title\":\"Tarea\"}",
                "{\"title\":\"Tarea\",\"priority\":null}",
                "{\"title\":\"Tarea\",\"priority\":\"URGENT\"}",
                "{\"title\":\"Tarea\",\"dueDate\":\"fecha\",\"priority\":\"HIGH\"}",
                "{invalido");
    }
}
