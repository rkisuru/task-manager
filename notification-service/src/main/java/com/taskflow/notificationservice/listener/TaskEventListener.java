package com.taskflow.notificationservice.listener;

import com.taskflow.notificationservice.dto.TaskCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens for TaskCompletedEvent messages from RabbitMQ.
 *
 * In a production system, this would send an email, push notification,
 * or Slack message. For this portfolio project, it logs the notification
 * to demonstrate the event-driven architecture works end-to-end.
 */
@Component
@Slf4j
public class TaskEventListener {

    @RabbitListener(queues = "task.completed.queue")
    public void handleTaskCompleted(TaskCompletedEvent event) {
        log.info("========================================");
        log.info("📬 NOTIFICATION: Task Completed!");
        log.info("   Task ID:      {}", event.getTaskId());
        log.info("   Title:        {}", event.getTitle());
        log.info("   Completed by: {}", event.getCompletedBy());
        log.info("   Completed at: {}", event.getCompletedAt());
        log.info("========================================");

        // In production, you'd send an email/notification here:
        // emailService.sendTaskCompletedEmail(event);
        // slackService.postMessage(event);
    }
}
