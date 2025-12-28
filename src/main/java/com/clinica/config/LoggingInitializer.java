package com.clinica.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Initializes the logs directory on application startup.
 * 
 * This component ensures that the logs directory exists before Logback
 * attempts to write log files, preventing FileNotFoundException errors.
 * 
 * The @PostConstruct annotation runs this method very early in the
 * Spring component lifecycle, before logging is fully initialized.
 */
@Component
public class LoggingInitializer {

    private static final String LOGS_DIR = "logs";

    /**
     * Creates the logs directory if it doesn't exist.
     * This method is invoked during Spring bean initialization,
     * which occurs before Logback tries to write files.
     */
    @PostConstruct
    public void initializeLoggingDirectory() {
        try {
            Path logsPath = Paths.get(LOGS_DIR);
            if (!Files.exists(logsPath)) {
                Files.createDirectories(logsPath);
            }
        } catch (Exception e) {
            // Log to System.err since logger might not be fully initialized
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }
}
