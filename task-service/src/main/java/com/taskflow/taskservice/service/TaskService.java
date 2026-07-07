package com.taskflow.taskservice.service;

import com.taskflow.taskservice.config.RabbitMQConfig;
import com.taskflow.taskservice.dto.CreateTaskRequest;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.dto.UpdateTaskRequest;
import com.taskflow.taskservice.entity.Task;
import com.taskflow.taskservice.entity.TaskPriority;
import com.taskflow.taskservice.entity.TaskStatus;
import com.taskflow.taskservice.event.TaskCompletedEvent;
import com.taskflow.taskservice.exception.ResourceNotFoundException;
import com.taskflow.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Create a new task assigned to the authenticated user.
     */
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, UUID assigneeId) {
        TaskPriority priority = TaskPriority.MEDIUM;
        if (request.getPriority() != null) {
            priority = TaskPriority.valueOf(request.getPriority().toUpperCase());
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(priority)
                .dueDate(request.getDueDate())
                .assigneeId(assigneeId)
                .status(TaskStatus.TODO)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created: {} by user {}", savedTask.getId(), assigneeId);

        return toResponse(savedTask);
    }

    /**
     * Get a task by ID.
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        return toResponse(task);
    }

    /**
     * Get all tasks for a specific user, optionally filtered by status.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(UUID assigneeId, String status) {
        List<Task> tasks;

        if (status != null && !status.isBlank()) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            tasks = taskRepository.findByAssigneeIdAndStatus(assigneeId, taskStatus);
        } else {
            tasks = taskRepository.findByAssigneeId(assigneeId);
        }

        return tasks.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Update a task. Publishes a TaskCompletedEvent if the task
     * transitions to DONE status.
     */
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        TaskStatus previousStatus = task.getStatus();

        // Apply partial updates — only update non-null fields
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getPriority() != null) {
            task.setPriority(TaskPriority.valueOf(request.getPriority().toUpperCase()));
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated: {}", updatedTask.getId());

        // Publish event if task just transitioned to DONE
        if (previousStatus != TaskStatus.DONE && updatedTask.getStatus() == TaskStatus.DONE) {
            publishTaskCompletedEvent(updatedTask, userId);
        }

        return toResponse(updatedTask);
    }

    /**
     * Delete a task by ID.
     */
    @Transactional
    public void deleteTask(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found: " + taskId);
        }
        taskRepository.deleteById(taskId);
        log.info("Task deleted: {}", taskId);
    }

    /**
     * Publish a TaskCompletedEvent to RabbitMQ.
     * This decouples task-service from notification-service — if the
     * notification service is down, task operations still succeed.
     */
    private void publishTaskCompletedEvent(Task task, UUID completedBy) {
        TaskCompletedEvent event = TaskCompletedEvent.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .completedBy(completedBy)
                .completedAt(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    event);
            log.info("Published TaskCompletedEvent for task: {}", task.getId());
        } catch (Exception e) {
            // Log but don't fail the task update — event publishing is best-effort
            log.error("Failed to publish TaskCompletedEvent for task {}: {}",
                    task.getId(), e.getMessage());
        }
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .assigneeId(task.getAssigneeId())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
