package com.taskapp;

import com.taskapp.domain.entity.Task;
import com.taskapp.domain.entity.TaskPriority;
import com.taskapp.domain.entity.TaskStatus;
import com.taskapp.repository.TaskRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskRepositoryTest {
    @Autowired TaskRepository repository;
    @Autowired EntityManager entityManager;

    @Test
    void persistsTaskWithGeneratedUuidAndAuditFields() {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");
        Task task = repository.saveAndFlush(new Task("Estudiar JPA", null, null,
                TaskStatus.OPEN, TaskPriority.HIGH, now, now));
        assertThat(task.getId()).isNotNull();
        entityManager.clear();

        Task stored = repository.findById(task.getId()).orElseThrow();
        assertThat(stored.getTitle()).isEqualTo("Estudiar JPA");
        assertThat(stored.getStatus()).isEqualTo(TaskStatus.OPEN);
        assertThat(stored.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(stored.getCreated()).isEqualTo(now);
        assertThat(stored.getUpdated()).isEqualTo(now);
    }
}
