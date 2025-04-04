package com.AI_powered_ERP_Solution.repository;

import com.AI_powered_ERP_Solution.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByJobId(String jobId);
    Optional<Job> findByAttachmentId(String attachmentId);
}