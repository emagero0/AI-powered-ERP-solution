package com.AI_powered_ERP_Solution.service;

import com.AI_powered_ERP_Solution.entity.Job;
import com.AI_powered_ERP_Solution.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public Optional<Job> findJobByJobId(String jobId) {
        return jobRepository.findByJobId(jobId);
    }

    public Optional<Job> findJobByAttachmentId(String attachmentId) {
        return jobRepository.findByAttachmentId(attachmentId);
    }

    public Job updateJobStatus(String jobId, String status) {
        Optional<Job> jobOptional = jobRepository.findByJobId(jobId);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setStatus(status);
            return jobRepository.save(job);
        } else {
            throw new RuntimeException("Job not found with jobId: " + jobId);
        }
    }

    public Job updateAttachment(String jobId, String attachmentId, byte[] attachmentData, String verificationResult) {
        Optional<Job> jobOptional = jobRepository.findByJobId(jobId);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setAttachmentId(attachmentId);
            job.setAttachmentData(attachmentData);
            job.setAttachmentVerificationResult(verificationResult);
            return jobRepository.save(job);
        } else {
            throw new RuntimeException("Job not found with jobId: " + jobId);
        }
    }
}