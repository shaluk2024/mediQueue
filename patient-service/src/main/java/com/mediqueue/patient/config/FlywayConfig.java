package com.mediqueue.patient.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

/**
 * Manual Flyway Configuration for MediQueue Patient Service.
 * * NOTE: This manual setup is used to ensure compatibility with Spring Boot 4.x
 * autoconfiguration requirements and to provide granular control over the
 * database migration lifecycle in a microservices' environment.
 */
@Configuration
public class FlywayConfig {

    // Pulling the migration script location from application.yml
    // Defaulting to the standard classpath:db/migration if not specified
    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String locations;

    // Baseline is true to allow Flyway to run on a database that
    // already contains existing tables (crucial for local dev resets)
    @Value("${spring.flyway.baseline-on-migrate:true}")
    private boolean baselineOnMigrate;

    /**
     * Bean definition for Flyway migration tool.
     * The 'initMethod = "migrate"' ensures that migrations run automatically
     * as soon as the Bean is initialized, before the JPA EntityManager starts.
     * * @param dataSource The primary PostgreSQL datasource
     * @return Configured Flyway instance
     */
    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .baselineOnMigrate(baselineOnMigrate)
                // Enables placeholder replacement in SQL files (e.g., ${tenant_id})
                .placeholderReplacement(true)
                // Ensures the migration fails fast if there is a checksum mismatch
                .validateOnMigrate(true)
                .load();
    }
}
