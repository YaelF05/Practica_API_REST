package com.taskapp;

import com.taskapp.domain.entity.Task;
import com.taskapp.domain.entity.TaskPriority;
import com.taskapp.domain.entity.TaskStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ListTasksApiTest extends TaskApiTestSupport {
    @Test
    void returnsEmptyArrayWhenNoTasksExist() throws Exception {
        mvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void returnsTasksInCreationOrderInsteadOfInsertionOrder() throws Exception {
        Instant first = Instant.parse("2026-01-01T10:00:00Z");
        Instant second = first.plusSeconds(60);
        repository.saveAndFlush(new Task("Segunda", null, null, TaskStatus.OPEN,
                TaskPriority.LOW, second, second));
        repository.saveAndFlush(new Task("Primera", null, null, TaskStatus.OPEN,
                TaskPriority.HIGH, first, first));

        mvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Primera"))
                .andExpect(jsonPath("$[1].title").value("Segunda"))
                .andExpect(jsonPath("$[0].created").doesNotExist());
    }
}
