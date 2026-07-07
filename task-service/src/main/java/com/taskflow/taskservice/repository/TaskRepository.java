package com.taskflow.taskservice.repository;

import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByAssigneeId(UUID assigneeId);

    List<Task> findByAssigneeIdAndStatus(UUID assigneeId, TaskStatus status);

    List<Task> findByStatus(TaskStatus status);
}
