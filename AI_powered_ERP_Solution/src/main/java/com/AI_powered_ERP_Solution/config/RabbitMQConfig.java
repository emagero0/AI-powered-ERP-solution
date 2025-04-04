package com.AI_powered_ERP_Solution.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.job-queue}")
    private String jobQueue;

    @Value("${app.rabbitmq.attachment-queue}")
    private String attachmentQueue;

    @Bean
    public Queue jobQueue() {
        return new Queue(jobQueue, true);
    }

    @Bean
    public Queue attachmentQueue() {
        return new Queue(attachmentQueue, true);
    }
}