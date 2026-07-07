package com.taskflow.taskservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request body for updating an existing task.
 * All fields are optional — only non-null fields are updated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private String status;    // TODO, IN_PROGRESS, DONE

    private String priority;  // LOW, MEDIUM, HIGH

    private LocalDate dueDate;
}
