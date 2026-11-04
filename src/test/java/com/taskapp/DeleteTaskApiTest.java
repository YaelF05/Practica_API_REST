package com.taskapp;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DeleteTaskApiTest extends TaskApiTestSupport {
    @Test
    void deletesTaskAndAllowsRepeatingTheRequest() throws Exception {
        String id = createTask("Eliminar").get("id").asText();
        mvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        assertThat(repository.existsById(UUID.fromString(id))).isFalse();
        mvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        mvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletingUnknownUuidReturns204() throws Exception {
        mvc.perform(delete("/api/v1/tasks/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void invalidUuidReturnsStandard400() throws Exception {
        mvc.perform(delete("/api/v1/tasks/no-es-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
