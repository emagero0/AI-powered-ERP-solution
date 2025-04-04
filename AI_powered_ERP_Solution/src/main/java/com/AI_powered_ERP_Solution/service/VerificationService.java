package com.AI_powered_ERP_Solution.service;

import com.AI_powered_ERP_Solution.entity.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    public String verifyJobDetails(Job job) {
        logger.info("Verifying job details for jobId: {}", job.getJobId());
        try {
            // Mock AI verification logic
            boolean isTitleValid = job.getTitle() != null && job.getTitle().toLowerCase().contains("sample");
            boolean isDescriptionValid = job.getDescription() != null && !job.getDescription().trim().isEmpty();

            if (isTitleValid && isDescriptionValid) {
                return "Verified";
            } else {
                String reason = "Verification failed: " +
                        (isTitleValid ? "" : "Title does not contain 'Sample'. ") +
                        (isDescriptionValid ? "" : "Description is empty.");
                return reason;
            }
        } catch (Exception e) {
            logger.error("Error during job verification for jobId: {}", job.getJobId(), e);
            return "Verification failed: " + e.getMessage();
        }
    }

    public String verifyAttachment(String attachmentData) {
        logger.info("Verifying attachment data");
        try {
            // Mock attachment verification (e.g., check if attachment data is non-empty)
            if (attachmentData != null && !attachmentData.trim().isEmpty()) {
                return "Attachment Verified";
            } else {
                return "Attachment verification failed: Attachment data is empty.";
            }
        } catch (Exception e) {
            logger.error("Error during attachment verification", e);
            return "Attachment verification failed: " + e.getMessage();
        }
    }
}