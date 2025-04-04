package com.AI_powered_ERP_Solution.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobId; // Dynamics 365 job ID
    private String title;
    private String description;
    private String status; // e.g., "Second Checked"
    private String verificationResult; // e.g., "Verified", "Failed"

    @Lob
    @Column(name = "attachment_data")
    private byte[] attachmentData; // Store attachment as BLOB

    private String attachmentId; // Dynamics 365 attachment ID
    private String attachmentVerificationResult; // e.g., "Attachment Verified"
}