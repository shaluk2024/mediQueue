package com.mediqueue.triage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediqueue.common.events.TriageCompletedEvent;
import com.mediqueue.triage.dto.TriageRequest;
import com.mediqueue.triage.dto.TriageResult;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TriageService {

    private final ChatClient chatClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    // Virtual threads for parallel calls (Java 17+ preview or use Executors.newFixedThreadPool)
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    private static final String TRIAGE_COMPLETED_TOPIC = "triage.completed";

    @CircuitBreaker(name = "llmService", fallbackMethod = "triageFallback")
    @Retry(name = "llmService")
    @Cacheable(value = "triageResults", key = "#request.appointmentId")
    public TriageResult triage(TriageRequest request) {
        log.info("Starting triage for appointment: {}", request.getAppointmentId());
        long start = System.currentTimeMillis();

        // MULTITHREADING: run AI call and symptom normalization in parallel
        // In a real system, you'd also fetch patient history here as a 3rd parallel call
        CompletableFuture<String> aiAnalysis = CompletableFuture
            .supplyAsync(() -> callGroqAI(request.getSymptoms()), executor);

        CompletableFuture<String> normalizedSymptoms = CompletableFuture
            .supplyAsync(() -> normalizeSymptoms(request.getSymptoms()), executor);

        // Wait for both
        CompletableFuture.allOf(aiAnalysis, normalizedSymptoms).join();

        long elapsed = System.currentTimeMillis() - start;
        log.info("Triage completed in {}ms for appointment: {}", elapsed, request.getAppointmentId());

        String rawAiResponse = aiAnalysis.join();
        TriageResult result = parseAiResponse(rawAiResponse, request.getAppointmentId());

        // Publish result to Kafka — Patient Service updates appointment priority
        publishTriageCompleted(request, result);

        return result;
    }

    // Fallback when circuit is open or all retries exhausted
    public TriageResult triageFallback(TriageRequest request, Throwable ex) {
        log.warn("Circuit breaker fallback triggered for appointment {}: {}",
            request.getAppointmentId(), ex.getMessage());

        TriageResult fallback = TriageResult.fallback(request.getAppointmentId());
        publishTriageCompleted(request, fallback);
        return fallback;
    }

    // Fallback for CallNotPermittedException (circuit is OPEN — skip the call entirely)
    public TriageResult triageFallback(TriageRequest request,
                                                   CallNotPermittedException ex) {
        log.warn("Circuit OPEN — skipping LLM call for appointment {}", request.getAppointmentId());
        TriageResult fallback = TriageResult.fallback(request.getAppointmentId());
        publishTriageCompleted(request, fallback);
        return fallback;
    }

    private String callGroqAI(String symptoms) {
        String prompt = """
            You are a medical triage assistant. Analyze the following symptoms and assess urgency.
            
            Symptoms: %s
            
            Respond ONLY with valid JSON in this exact format (no markdown, no extra text):
            {
              "priority": "LOW|MEDIUM|HIGH|CRITICAL",
              "reasoning": "brief clinical reasoning in one sentence",
              "confidence": 0.0-1.0
            }
            
            Priority guide:
            - CRITICAL: chest pain, difficulty breathing, stroke symptoms, severe bleeding
            - HIGH: high fever, severe pain, acute allergic reaction
            - MEDIUM: moderate pain, persistent symptoms, chronic condition flare
            - LOW: mild symptoms, routine follow-up, minor complaints
            """.formatted(symptoms);

        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }

    private TriageResult parseAiResponse(String rawResponse, String appointmentId) {
        try {
            // Strip any markdown fences just in case
            String clean = rawResponse.replaceAll("```json|```", "").trim();
            var node = objectMapper.readTree(clean);

            return TriageResult.builder()
                .appointmentId(appointmentId)
                .priority(node.get("priority").asText("MEDIUM"))
                .reasoning(node.get("reasoning").asText("Assessment complete"))
                .confidence(node.get("confidence").asDouble(0.8))
                .aiAvailable(true)
                .build();
        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", rawResponse, e);
            return TriageResult.fallback(appointmentId);
        }
    }

    private String normalizeSymptoms(String symptoms) {
        return Arrays.stream(symptoms.toLowerCase().split("[,;]"))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .sorted()
            .collect(Collectors.joining(", "));
    }

    private void publishTriageCompleted(TriageRequest request,
                                         TriageResult result) {
        TriageCompletedEvent event = TriageCompletedEvent.builder()
            .appointmentId(request.getAppointmentId())
            .patientId(request.getPatientId())
            .priority(result.getPriority())
            .reasoning(result.getReasoning())
            .aiAvailable(result.isAiAvailable())
            .build();

        kafkaTemplate.send(TRIAGE_COMPLETED_TOPIC, request.getAppointmentId(), event);
    }
}
