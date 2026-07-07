package com.taskflow.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response body for task data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private UUID assigneeId;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
