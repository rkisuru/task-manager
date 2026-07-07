package com.taskflow.taskservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCompletedEvent implements Serializable {

    private UUID taskId;
    private String title;
    private UUID completedBy;
    private LocalDateTime completedAt;
}
