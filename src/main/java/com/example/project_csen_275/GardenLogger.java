package com.example.project_csen_275;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
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
        String emoji = switch (level) {
            case "INFO" -> "ℹ️ ";
            case "WARNING" -> "⚠️ ";
            case "ERROR" -> "❌ ";
            case "EVENT" -> "🔔 ";
            default -> "";
        };

        // Create the formatted message with emoji
        final String formattedMessage = timestamp + " " + emoji + "[" + level + "] " + message;

        // Update the UI on the JavaFX application thread
        Platform.runLater(() -> {
            // Add to the beginning of the list so the newest logs are at top
            logs.addFirst(formattedMessage);

            // Keep the list at a reasonable size
            if (logs.size() > MAX_LOGS) {
                logs.removeLast();
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
        Platform.runLater(logs::clear);
    }

    /**
     * Write a log message to a file
     * 
     * @param message The message to write
     */
    private static void writeToLogFile(String message) {
        // Create logs directory if it doesn't exist
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            if (!logDir.mkdir()) {
                System.err.println("Error: Could not create logs directory at " + logDir.getAbsolutePath());
                return;
            }
        }

        // Create log file with date in filename
        String today = fileDateFormat.format(new Date());
        String logFileName = LOG_DIRECTORY + File.separator + LOG_FILE_PREFIX + today + ".log";

        try (FileWriter fw = new FileWriter(logFileName, true);
                PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error: Log file not found or cannot be created: " + e.getMessage());
            System.err.println("Log directory permissions may be incorrect for: " + logFileName);
        } catch (java.io.IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Security violation: No permission to write logs: " + e.getMessage());
        }
    }

}
