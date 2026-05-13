package com.mediqueue.triage.service;

import com.mediqueue.common.events.AppointmentBookedEvent;
import com.mediqueue.triage.dto.TriageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentEventConsumer {

    private final TriageService triageService;

    // Auto-retry with exponential backoff — failed messages go to DLQ after 3 attempts
    @RetryableTopic(
        attempts = "3",
        backOff = @BackOff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = ".dlq"
    )
    @KafkaListener(topics = "${kafka.topics.appointment-booked}", groupId = "triage-group")
    public void onAppointmentBooked(AppointmentBookedEvent event) {
        log.info("Received appointment booked event: {}", event.getAppointmentId());

        if (event.getSymptoms() == null || event.getSymptoms().isBlank()) {
            log.info("No symptoms provided for appointment {}, skipping triage", event.getAppointmentId());
            return;
        }

        TriageRequest request = TriageRequest.builder()
            .appointmentId(event.getAppointmentId())
            .patientId(event.getPatientId())
            .symptoms(event.getSymptoms())
            .doctorId(event.getDoctorId())
            .build();

        triageService.triage(request);
    }

    // Dead letter handler — log and alert (could also store in DB for manual review)
    @DltHandler
    public void handleDlt(AppointmentBookedEvent event) {
        log.error("DLT: Triage permanently failed for appointment: {}. " +
                  "Manual triage required for patient: {}",
                  event.getAppointmentId(), event.getPatientId());
        // In production: send alert to admin, store in failed_triage table
    }
}
