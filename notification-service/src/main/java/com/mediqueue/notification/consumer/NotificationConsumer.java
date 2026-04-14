package com.mediqueue.notification.consumer;

import com.mediqueue.notification.events.*;
import com.mediqueue.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka consumer responsible for handling notification-related events.
 * <p>
 * This component listens to various appointment lifecycle events published by
 * other microservices (e.g., patient-service) and triggers appropriate email
 * notifications using {@link EmailService}.
 * </p>
 *
 * <p>
 * It leverages Spring Kafka's {@link RetryableTopic} to provide non-blocking retries
 * with exponential backoff and Dead Letter Topic (DLT) support.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@EnableKafkaRetryTopic
public class NotificationConsumer {

    /**
     * Service responsible for sending emails.
     */
    private final EmailService emailService;

    // In-memory idempotency store — use Redis in production for multi-instance deployments
    // Key: appointmentId + eventType, prevents duplicate emails if Kafka retries
    /**
     * Tracks processed events to prevent duplicate email notifications.
     * <p>
     * This ensures idempotent behavior when Kafka retries the same message.
     * </p>
     */
    private final Set<String> processedEvents = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Consumes appointment booked events and sends confirmation email.
     * <p>
     * Retries are handled using retry topics with exponential backoff.
     * </p>
     *
     * @param event appointment booking event payload
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".dlq"
    )
    @KafkaListener(topics = "${kafka.topics.appointment-booked}", groupId = "notification-group")
    public void onAppointmentBooked(AppointmentBookedEvent event) {

        // Generate idempotency key to avoid duplicate processing
        String key = event.getAppointmentId() + ":BOOKED";

        // Skip if already processed (prevents duplicate emails due to retries)
        if (!processedEvents.add(key)) {
            log.warn("Duplicate event skipped: {}", key);
            return;
        }

        // Log and send confirmation email
        log.info("Sending booking confirmation to: {}", event.getPatientEmail());
        emailService.sendAppointmentConfirmation(
                event.getPatientEmail(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getSlotDate() != null ? event.getSlotDate().toString() : "N/A",
                event.getStartTime() != null ? event.getStartTime().toString() : "N/A",
                event.getAppointmentId()
        );
    }

    /**
     * Consumes appointment cancellation events and sends cancellation email.
     *
     * @param event appointment cancellation event payload
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".dlq"
    )
    @KafkaListener(topics = "${kafka.topics.appointment-cancelled}", groupId = "notification-group")
    public void onAppointmentCancelled(AppointmentCancelledEvent event) {

        // Generate idempotency key
        String key = event.getAppointmentId() + ":CANCELLED";

        // Prevent duplicate processing
        if (!processedEvents.add(key)) {
            log.warn("Duplicate event skipped: {}", key);
            return;
        }

        // Log and send cancellation email
        log.info("Sending cancellation confirmation to: {}", event.getPatientEmail());
        emailService.sendCancellationConfirmation(
                event.getPatientEmail(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getSlotDate() != null ? event.getSlotDate().toString() : "N/A",
                event.getStartTime() != null ? event.getStartTime().toString() : "N/A"
        );
    }

    /**
     * Consumes triage completion events.
     * <p>
     * Sends notification only if AI triage is available.
     * </p>
     *
     * @param event triage completion event payload
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".dlq"
    )
    @KafkaListener(topics = "${kafka.topics.triage-completed}", groupId = "notification-group")
    public void onTriageCompleted(TriageCompletedEvent event) {

        // Generate idempotency key
        String key = event.getAppointmentId() + ":TRIAGE";

        // Prevent duplicate processing
        if (!processedEvents.add(key)) {
            log.warn("Duplicate triage event skipped: {}", key);
            return;
        }

        // Skip if AI triage was not available
        if (!event.isAiAvailable()) {
            log.info("AI was unavailable for appointment {} — skipping triage email", event.getAppointmentId());
            return;
        }

        // In production you'd look up patient email by patientId here
        // For demo we log the triage result
        log.info("Triage completed for appointment {}: priority={}",
                event.getAppointmentId(), event.getPriority());
    }

    /**
     * Consumes queue update events and logs updated queue position.
     *
     * @param event queue update event payload
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".dlq"
    )
    @KafkaListener(topics = "${kafka.topics.queue-updated}", groupId = "notification-group")
    public void onQueueUpdated(QueueUpdatedEvent event) {

        // Generate idempotency key (includes position to track changes)
        String key = event.getAppointmentId() + ":QUEUE:" + event.getNewQueuePosition();

        // Prevent duplicate processing
        if (!processedEvents.add(key)) {
            log.warn("Duplicate queue update skipped: {}", key);
            return;
        }

        // Log queue update
        log.info("Queue updated for appointment {}: position={}",
                event.getAppointmentId(), event.getNewQueuePosition());
    }

    /**
     * Handles messages that failed all retry attempts and were sent to the Dead Letter Topic (DLT).
     * <p>
     * This is the final stage of failure handling where manual intervention or alerting is typically triggered.
     * </p>
     *
     * @param event failed event payload
     */
    @DltHandler
    public void handleDlt(Object event) {
        log.error("DLT: Notification permanently failed for event: {}", event);
        // In production: store in failed_notifications table, trigger admin alert
    }
}