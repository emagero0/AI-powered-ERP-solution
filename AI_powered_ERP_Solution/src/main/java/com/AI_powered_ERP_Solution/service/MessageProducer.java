package com.AI_powered_ERP_Solution.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.job-queue}")
    private String jobQueue;

    @Value("${app.rabbitmq.attachment-queue}")
    private String attachmentQueue;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendJobMessage(String jobId, String jobDetails) {
        rabbitTemplate.convertAndSend(jobQueue, new JobMessage(jobId, jobDetails));
    }

    public void sendAttachmentMessage(String jobId, String attachmentId, String attachmentData) {
        rabbitTemplate.convertAndSend(attachmentQueue, new AttachmentMessage(jobId, attachmentId, attachmentData));
    }

    // Message classes
    public static record JobMessage(String jobId, String jobDetails) {}

    public static record AttachmentMessage(String jobId, String attachmentId, String attachmentData) {}
}