package com.mediqueue.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending email notifications to patients.
 * <p>
 * This service is used by Kafka consumers to notify users about
 * appointment events such as booking, cancellation, triage updates,
 * and queue position changes.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    /**
     * Spring-provided mail sender used to dispatch emails.
     */
    private final JavaMailSender mailSender;

    /**
     * Configured sender email address (injected from application properties).
     */
    @Value("${notification.from-email}")
    private String fromEmail;

    /**
     * Configured sender display name (injected from application properties).
     */
    @Value("${notification.from-name}")
    private String fromName;

    /**
     * Sends appointment booking confirmation email to the patient.
     *
     * @param to recipient email address
     * @param patientName name of the patient
     * @param doctorName name of the doctor
     * @param date appointment date (formatted)
     * @param time appointment time (formatted)
     * @param appointmentId unique appointment identifier
     */
    public void sendAppointmentConfirmation(String to, String patientName,
                                            String doctorName, String date,
                                            String time, String appointmentId) {
        String subject = "Appointment Confirmed — MediQueue";

        // Email body using Java text block for readability
        String body = """
            Dear %s,
            
            Your appointment has been confirmed.
            
            Doctor: Dr. %s
            Date: %s
            Time: %s
            Appointment ID: %s
            
            Our AI triage system will analyze your symptoms shortly and may update your queue position based on medical urgency.
            
            Please arrive 10 minutes early.
            
            Best regards,
            MediQueue Team
            """.formatted(patientName, doctorName, date, time, appointmentId);

        sendEmail(to, subject, body);
    }

    /**
     * Sends appointment cancellation email to the patient.
     *
     * @param to recipient email address
     * @param patientName name of the patient
     * @param doctorName name of the doctor
     * @param date scheduled appointment date
     * @param time scheduled appointment time
     */
    public void sendCancellationConfirmation(String to, String patientName,
                                             String doctorName, String date, String time) {
        String subject = "Appointment Cancelled — MediQueue";

        // Informational email notifying cancellation
        String body = """
            Dear %s,
            
            Your appointment with Dr. %s on %s at %s has been cancelled.
            
            You can book a new appointment at any time through the MediQueue portal.
            
            Best regards,
            MediQueue Team
            """.formatted(patientName, doctorName, date, time);

        sendEmail(to, subject, body);
    }

    /**
     * Sends triage result update to the patient.
     *
     * @param to recipient email address
     * @param patientName name of the patient
     * @param priority triage priority (e.g., CRITICAL, HIGH, MEDIUM, LOW)
     * @param reasoning explanation of triage result
     */
    public void sendTriageUpdate(String to, String patientName,
                                 String priority, String reasoning) {

        String subject = "Your Triage Priority — MediQueue";

        // Maps priority to a user-friendly urgency label
        String urgencyLabel = switch (priority) {
            case "CRITICAL" -> "CRITICAL — you will be seen first";
            case "HIGH"     -> "HIGH — priority queue";
            case "MEDIUM"   -> "MEDIUM — standard queue";
            default         -> "LOW — routine queue";
        };

        // Email body describing triage assessment
        String body = """
            Dear %s,
            
            Our AI triage system has assessed your symptoms.
            
            Priority: %s
            Assessment: %s
            
            Your queue position has been updated accordingly. You will receive further updates closer to your appointment.
            
            Best regards,
            MediQueue Team
            """.formatted(patientName, urgencyLabel, reasoning);

        sendEmail(to, subject, body);
    }

    /**
     * Sends queue position update email to the patient.
     *
     * @param to recipient email address
     * @param patientName name of the patient
     * @param queuePosition updated queue position
     */
    public void sendQueueUpdate(String to, String patientName, int queuePosition) {
        String subject = "Queue Update — MediQueue";

        // Simple notification about queue position change
        String body = """
            Dear %s,
            
            Your queue position has been updated to: #%d
            
            Best regards,
            MediQueue Team
            """.formatted(patientName, queuePosition);

        sendEmail(to, subject, body);
    }

    /**
     * Core method responsible for constructing and sending email.
     * <p>
     * This method uses {@link SimpleMailMessage} for sending plain-text emails.
     * It logs success and failure for observability and debugging.
     * </p>
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email content
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            // Create simple text-based email message
            SimpleMailMessage message = new SimpleMailMessage();

            // Set sender in "Name <email>" format
            message.setFrom(fromName + " <" + fromEmail + ">");

            // Set recipient, subject, and body
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            // Send email via configured mail sender
            mailSender.send(message);

            // Log successful delivery
            log.info("Email sent to: {} | Subject: {}", to, subject);

        } catch (Exception e) {
            // Log failure details for debugging/monitoring
            log.error("Failed to send email to {}: {}", to, e.getMessage());

            // Rethrow exception to allow Kafka retry/DLQ mechanisms to handle failure
            throw e;

            // Don't rethrow — email failure should not cause Kafka offset commit failure
        }
    }
}