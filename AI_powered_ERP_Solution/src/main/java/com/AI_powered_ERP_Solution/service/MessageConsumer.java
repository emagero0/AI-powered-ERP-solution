package com.AI_powered_ERP_Solution.service;

import com.AI_powered_ERP_Solution.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final JobService jobService;
    private final VerificationService verificationService;

    public MessageConsumer(JobService jobService, VerificationService verificationService) {
        this.jobService = jobService;
        this.verificationService = verificationService;
    }

    @RabbitListener(queues = "${app.rabbitmq.job-queue}")
    public void processJobMessage(MessageProducer.JobMessage message) {
        try {
            logger.info("Processing job message for jobId: {}", message.jobId());

            // Parse job details (simplified; in reality, parse JSON)
            Job job = new Job();
            job.setJobId(message.jobId());
            job.setTitle("Sample Job Title");
            job.setDescription("Sample Job Description");
            job.setStatus("Fetched");

            // Verify job details
            String verificationResult = verificationService.verifyJobDetails(job);
            job.setVerificationResult(verificationResult);

            // Save to database
            jobService.saveJob(job);

            logger.info("Job processed successfully: {}", verificationResult);
        } catch (Exception e) {
            logger.error("Error processing job message for jobId: {}", message.jobId(), e);
            // Optionally, send to a dead-letter queue or retry
            throw new RuntimeException("Failed to process job message", e);
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.attachment-queue}")
    public void processAttachmentMessage(MessageProducer.AttachmentMessage message) {
        try {
            logger.info("Processing attachment message for jobId: {}, attachmentId: {}", message.jobId(), message.attachmentId());

            // Verify attachment
            String verificationResult = verificationService.verifyAttachment(message.attachmentData());

            // Convert attachment to byte[]
            byte[] attachmentData = message.attachmentData().getBytes();

            // Update job with attachment data
            Job updatedJob = jobService.updateAttachment(message.jobId(), message.attachmentId(), attachmentData, verificationResult);

            logger.info("Attachment processed successfully: {}", verificationResult);
        } catch (Exception e) {
            logger.error("Error processing attachment message for jobId: {}, attachmentId: {}", message.jobId(), message.attachmentId(), e);
            // Optionally, send to a dead-letter queue or retry
            throw new RuntimeException("Failed to process attachment message", e);
        }
    }
}