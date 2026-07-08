package com.taskflow.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO matching the TaskCompletedEvent published by task-service.
 * Deserialized from the JSON message on the RabbitMQ queue.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletedEvent implements Serializable {

    private UUID taskId;
    private String title;
    private UUID completedBy;
    private LocalDateTime completedAt;
}
