package com.AI_powered_ERP_Solution.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class Dynamics365Service {

    private static final Logger logger = LoggerFactory.getLogger(Dynamics365Service.class);

    @Value("${dynamics365.api.base-url}")
    private String apiBaseUrl;

    private final WebClient webClient;

    public Dynamics365Service(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getJobDetails(String jobId, @RegisteredOAuth2AuthorizedClient("dynamics365") OAuth2AuthorizedClient authorizedClient) {
        try {
            return webClient.get()
                    .uri(apiBaseUrl + "/jobs(" + jobId + ")")
                    .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            logger.error("Failed to fetch job details for jobId: {}", jobId, e);
            throw new RuntimeException("Failed to fetch job details", e);
        }
    }

    public String getJobAttachment(String jobId, String attachmentId, OAuth2AuthorizedClient authorizedClient) {
        try {
            return webClient.get()
                    .uri(apiBaseUrl + "/jobs(" + jobId + ")/attachments(" + attachmentId + ")")
                    .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            logger.error("Failed to fetch attachment for jobId: {}, attachmentId: {}", jobId, attachmentId, e);
            throw new RuntimeException("Failed to fetch attachment", e);
        }
    }

    public void updateJobStatus(String jobId, String status, OAuth2AuthorizedClient authorizedClient) {
        try {
            webClient.patch()
                    .uri(apiBaseUrl + "/jobs(" + jobId + ")")
                    .header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue())
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .bodyValue("{\"status\": \"" + status + "\"}")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            logger.error("Failed to update status for jobId: {}, status: {}", jobId, status, e);
            throw new RuntimeException("Failed to update job status", e);
        }
    }
}