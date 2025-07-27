package com.example.project_csen_275;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger class for garden operations
 */
public class GardenLogger {
    // Observable list to store logs for display in the GUI
    private static final ObservableList<String> logs = FXCollections.observableArrayList();
    private static final int MAX_LOGS = 100; // Maximum number of logs to keep
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE_PREFIX = "garden_log_";
    private static boolean fileLoggingEnabled = true;

    /**
     * Log an information message
     * 
     * @param message The message to log
     */
    public static void info(String message) {
        addLog("INFO", message);
    }

    /**
     * Log a warning message
     * 
     * @param message The warning message to log
     */
    public static void warning(String message) {
        addLog("WARNING", message);
    }

    /**
     * Log an error message
     * 
     * @param message The error message to log
     */
    public static void error(String message) {
        addLog("ERROR", message);
    }

    /**
     * Log a garden event
     * 
     * @param message The event message to log
     */
    public static void event(String message) {
        addLog("EVENT", message);
    }

    /**
     * Add a log entry with timestamp and level
     * 
     * @param level   The log level
     * @param message The message to log
     */
    private static void addLog(String level, String message) {
        String timestamp = dateFormat.format(new Date());

        // Add appropriate emoji based on log level
        String emoji = "";
        switch (level) {
            case "INFO":
                emoji = "ℹ️ ";
                break;
            case "WARNING":
                emoji = "⚠️ ";
                break;
            case "ERROR":
                emoji = "❌ ";
                break;
            case "EVENT":
                emoji = "🔔 ";
                break;
        }

        // Create the formatted message with emoji
        final String formattedMessage = timestamp + " " + emoji + "[" + level + "] " + message;

        // Update the UI on the JavaFX application thread
        Platform.runLater(() -> {
            // Add to the beginning of the list so newest logs are at top
            logs.add(0, formattedMessage);
            
            // Keep the list at a reasonable size
            if (logs.size() > MAX_LOGS) {
                logs.remove(logs.size() - 1);
            }
        });

        // Also print to console for debugging (safe to do on any thread)
        System.out.println(formattedMessage);

        // Write to log file
        if (fileLoggingEnabled) {
            writeToLogFile(formattedMessage);
        }
    }

    /**
     * Get the observable list of logs
     * 
     * @return Observable list of log entries
     */
    public static ObservableList<String> getLogs() {
        return logs;
    }

    /**
     * Clear all logs
     */
    public static void clearLogs() {
        Platform.runLater(() -> logs.clear());
    }

    /**
     * Write a log message to a file
     * 
     * @param message The message to write
     */
    private static void writeToLogFile(String message) {
        try {
            // Create logs directory if it doesn't exist
            File logDir = new File(LOG_DIRECTORY);
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            // Create log file with date in filename
            String today = fileDateFormat.format(new Date());
            String logFileName = LOG_DIRECTORY + File.separator + LOG_FILE_PREFIX + today + ".log";

            // Append to log file
            try (FileWriter fw = new FileWriter(logFileName, true);
                    PrintWriter pw = new PrintWriter(fw)) {
                pw.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Enable or disable file logging
     * 
     * @param enabled Whether file logging should be enabled
     */
    public static void setFileLoggingEnabled(boolean enabled) {
        fileLoggingEnabled = enabled;
    }

    /**
     * Check if file logging is enabled
     * 
     * @return true if file logging is enabled
     */
    public static boolean isFileLoggingEnabled() {
        return fileLoggingEnabled;
    }
}
