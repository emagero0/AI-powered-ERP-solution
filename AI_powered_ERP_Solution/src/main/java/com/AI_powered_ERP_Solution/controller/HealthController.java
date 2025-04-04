package com.AI_powered_ERP_Solution.controller;

import com.AI_powered_ERP_Solution.entity.Job;
import com.AI_powered_ERP_Solution.service.Dynamics365Service;
import com.AI_powered_ERP_Solution.service.JobService;
import com.AI_powered_ERP_Solution.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class HealthController {

    private final Dynamics365Service dynamics365Service;
    private final JobService jobService;
    private final VerificationService verificationService;

    public HealthController(Dynamics365Service dynamics365Service, JobService jobService, VerificationService verificationService) {
        this.dynamics365Service = dynamics365Service;
        this.jobService = jobService;
        this.verificationService = verificationService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<String> healthCheck() {
        try {
            return ResponseEntity.ok("API is up and running!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("API is down: " + e.getMessage());
        }
    }

    @GetMapping("/api/job")
    public ResponseEntity<String> getJob(
            @RequestParam String jobId,
            @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            // Fetch job details from Dynamics 365
            String jobDetails = dynamics365Service.getJobDetails(jobId, authorizedClient);

            // Parse the job details (simplified; in reality, parse JSON)
            Job job = new Job();
            job.setJobId(jobId);
            job.setTitle("Sample Job Title"); // Replace with actual parsing
            job.setDescription("Sample Job Description"); // Replace with actual parsing
            job.setStatus("Fetched");

            // Verify job details
            String verificationResult = verificationService.verifyJobDetails(job);
            job.setVerificationResult(verificationResult);

            // Save to database
            jobService.saveJob(job);

            return ResponseEntity.ok("Job fetched and verified: " + verificationResult);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching job: " + e.getMessage());
        }
    }

    @GetMapping("/api/job/attachment")
    public ResponseEntity<String> getAttachment(
            @RequestParam String jobId,
            @RequestParam String attachmentId,
            @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            // Fetch attachment from Dynamics 365
            String attachment = dynamics365Service.getJobAttachment(jobId, attachmentId, authorizedClient);

            // Verify attachment
            String verificationResult = verificationService.verifyAttachment(attachment);

            // Convert attachment to byte[] (assuming it's a string; adjust based on actual format)
            byte[] attachmentData = attachment.getBytes();

            // Update job with attachment data
            Job updatedJob = jobService.updateAttachment(jobId, attachmentId, attachmentData, verificationResult);

            return ResponseEntity.ok("Attachment fetched, verified, and stored: " + verificationResult);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching attachment: " + e.getMessage());
        }
    }

    @GetMapping("/api/job/attachment/data")
    public ResponseEntity<byte[]> getAttachmentData(@RequestParam String attachmentId) {
        try {
            Optional<Job> jobOptional = jobService.findJobByAttachmentId(attachmentId);
            if (jobOptional.isPresent()) {
                Job job = jobOptional.get();
                byte[] attachmentData = job.getAttachmentData();
                if (attachmentData != null) {
                    return ResponseEntity.ok()
                            .header("Content-Disposition", "attachment; filename=\"attachment-" + attachmentId + ".txt\"")
                            .body(attachmentData);
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/api/job/status")
    public ResponseEntity<String> updateStatus(
            @RequestParam String jobId,
            @RequestParam String status,
            @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            // Update status in Dynamics 365
            dynamics365Service.updateJobStatus(jobId, status, authorizedClient);

            // Update status in local database
            Job updatedJob = jobService.updateJobStatus(jobId, status);

            // Re-verify job details after status update
            String verificationResult = verificationService.verifyJobDetails(updatedJob);
            updatedJob.setVerificationResult(verificationResult);
            jobService.saveJob(updatedJob);

            return ResponseEntity.ok("Job status updated to: " + updatedJob.getStatus() + ", Verification: " + verificationResult);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating status: " + e.getMessage());
        }
    }
}