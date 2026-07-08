package com.taskflow.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration — declares the same exchange, queue, and binding
 * as task-service to ensure the infrastructure exists on both ends.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "taskflow.exchange";
    public static final String QUEUE_NAME = "task.completed.queue";
    public static final String ROUTING_KEY = "task.completed";

    @Bean
    public TopicExchange taskflowExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue taskCompletedQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding taskCompletedBinding(Queue taskCompletedQueue, TopicExchange taskflowExchange) {
        return BindingBuilder
                .bind(taskCompletedQueue)
                .to(taskflowExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
