package com.AI_powered_ERP_Solution.controller;

import com.AI_powered_ERP_Solution.entity.Job;
import com.AI_powered_ERP_Solution.service.Dynamics365Service;
import com.AI_powered_ERP_Solution.service.JobService;
import com.AI_powered_ERP_Solution.service.MessageProducer;
import com.AI_powered_ERP_Solution.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final MessageProducer messageProducer;

    public HealthController(Dynamics365Service dynamics365Service, JobService jobService,
                            VerificationService verificationService, MessageProducer messageProducer) {
        this.dynamics365Service = dynamics365Service;
        this.jobService = jobService;
        this.verificationService = verificationService;
        this.messageProducer = messageProducer;
    }

    @Operation(summary = "Check API health", description = "Returns the health status of the API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API is up and running"),
            @ApiResponse(responseCode = "500", description = "API is down with an error")
    })
    @GetMapping("/api/health")
    public ResponseEntity<String> healthCheck() {
        try {
            return ResponseEntity.ok("API is up and running!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("API is down: " + e.getMessage());
        }
    }

    @Operation(summary = "Fetch job details", description = "Fetches job details from Dynamics 365 and queues them for processing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job fetch request queued successfully"),
            @ApiResponse(responseCode = "500", description = "Error queuing job fetch request")
    })
    @GetMapping("/api/job")
    public ResponseEntity<String> getJob(
            @Parameter(description = "ID of the job to fetch", required = true) @RequestParam String jobId,
            @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            String jobDetails = dynamics365Service.getJobDetails(jobId, authorizedClient).block();
            messageProducer.sendJobMessage(jobId, jobDetails);
            return ResponseEntity.ok("Job fetch request queued for processing");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error queuing job fetch request: " + e.getMessage());
        }
    }

    @Operation(summary = "Fetch job attachment", description = "Fetches an attachment for a job from Dynamics 365 and queues it for processing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment fetch request queued successfully"),
            @ApiResponse(responseCode = "500", description = "Error queuing attachment fetch request")
    })
    @GetMapping("/api/job/attachment")
    public ResponseEntity<String> getAttachment(
            @Parameter(description = "ID of the job", required = true) @RequestParam String jobId,
            @Parameter(description = "ID of the attachment to fetch", required = true) @RequestParam String attachmentId,
            @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            String attachment = dynamics365Service.getJobAttachment(jobId, attachmentId, authorizedClient).block();
            messageProducer.sendAttachmentMessage(jobId, attachmentId, attachment);
            return ResponseEntity.ok("Attachment fetch request queued for processing");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error queuing attachment fetch request: " + e.getMessage());
        }
    }

    @Operation(summary = "Retrieve stored attachment", description = "Retrieves a stored attachment from the database by attachment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Attachment not found"),
            @ApiResponse(responseCode = "500", description = "Error retrieving attachment")
    })
    @GetMapping("/api/job/attachment/data")
    public ResponseEntity<byte[]> getAttachmentData(
            @Parameter(description = "ID of the attachment to retrieve", required = true) @RequestParam String attachmentId) {
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

    @Operation(summary = "Update job status", description = "Updates the status of a job in Dynamics 365 and the local database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job status updated successfully"),
            @ApiResponse(responseCode = "500", description = "Error updating job status")
    })
    @PostMapping("/api/job/status")
    public ResponseEntity<String> updateStatus(
            @Parameter(description = "ID of the job to update", required = true) @RequestParam String jobId,
            @Parameter(description = "New status for the job", required = true) @RequestParam String status,
            @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            dynamics365Service.updateJobStatus(jobId, status, authorizedClient);
            Job updatedJob = jobService.updateJobStatus(jobId, status);
            String verificationResult = verificationService.verifyJobDetails(updatedJob);
            updatedJob.setVerificationResult(verificationResult);
            jobService.saveJob(updatedJob);
            return ResponseEntity.ok("Job status updated to: " + updatedJob.getStatus() + ", Verification: " + verificationResult);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating status: " + e.getMessage());
        }
    }
}