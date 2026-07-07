package com.taskflow.taskservice.controller;

import com.taskflow.taskservice.dto.CreateTaskRequest;
import com.taskflow.taskservice.dto.TaskResponse;
import com.taskflow.taskservice.dto.UpdateTaskRequest;
import com.taskflow.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task.
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @RequestAttribute("userId") String userId) {
        TaskResponse response = taskService.createTask(request, UUID.fromString(userId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a single task by ID.
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable UUID id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all tasks for the authenticated user, optionally filtered by status.
     * GET /api/tasks?status=TODO
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestAttribute("userId") String userId,
            @RequestParam(required = false) String status) {
        List<TaskResponse> tasks = taskService.getTasksByAssignee(UUID.fromString(userId), status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Update a task.
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @RequestAttribute("userId") String userId) {
        TaskResponse response = taskService.updateTask(id, request, UUID.fromString(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a task.
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
