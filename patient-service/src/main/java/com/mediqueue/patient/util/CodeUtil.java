package com.mediqueue.patient.util;

import java.security.SecureRandom;

/**
 * Utility class for generating unique human-readable codes
 * for MediQueue entities.
 *
 * Code format: PREFIX-XXX-YYY
 * Where XXX and YYY are random 3-digit numbers (100-999)
 *
 * Examples:
 * - Patient  → PAT-123-456
 * - Doctor   → DOC-765-890
 * - Slot     → SLT-234-567
 * - Appointment → APT-890-123
 *
 * Uses SecureRandom instead of Random for better randomness.
 *
 * Note: These codes are for human-readable identification only.
 * Internal DB operations still use Long id as primary key.
 */
public class CodeUtil {

    /**
     * SecureRandom is preferred over Random because:
     * - Cryptographically stronger random number generation
     * - Harder to predict next generated value
     * - Recommended for any ID/token generation
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Minimum value for each 3-digit segment.
     * Ensures codes are always 3 digits (never 001 or 099).
     */
    private static final int MIN = 100;

    /**
     * Range for random number generation.
     * MIN + RANGE - 1 = 999 (maximum 3-digit number)
     */
    private static final int RANGE = 900; // 100 to 999

    // Private constructor — utility class should never be instantiated
    private CodeUtil() {
        throw new UnsupportedOperationException("CodeUtil is a utility class");
    }

    /**
     * Generates a unique patient code.
     * Format: PAT-XXX-YYY
     * Example: PAT-234-789
     */
    public static String generatePatientCode() {
        return generateCode("PAT");
    }

    /**
     * Generates a unique doctor code.
     * Format: DOC-XXX-YYY
     * Example: DOC-765-890
     */
    public static String generateDoctorCode() {
        return generateCode("DOC");
    }

    /**
     * Generates a unique doctor slot code.
     * Format: SLT-XXX-YYY
     * Example: SLT-234-567
     */
    public static String generateSlotCode() {
        return generateCode("SLT");
    }

    /**
     * Generates a unique appointment code.
     * Format: APT-XXX-YYY
     * Example: APT-890-123
     */
    public static String generateAppointmentCode() {
        return generateCode("APT");
    }

    /**
     * Generates a unique user code.
     * Format: USR-XXX-YYY
     * Example: USR-890-123
     */
    public static String generateUserCode() {
        return generateCode("USR");
    }

    /**
     * Core code generation logic.
     *
     * Generates two independent 3-digit random numbers
     * and combines them with the prefix.
     *
     * @param prefix Entity-specific prefix (PAT, DOC, SLT, APT)
     * @return Formatted code string: PREFIX-XXX-YYY
     */
    private static String generateCode(String prefix) {
        int first  = MIN + RANDOM.nextInt(RANGE); // 100–999
        int second = MIN + RANDOM.nextInt(RANGE); // 100–999
        return String.format("%s-%d-%d", prefix, first, second);
    }
}
