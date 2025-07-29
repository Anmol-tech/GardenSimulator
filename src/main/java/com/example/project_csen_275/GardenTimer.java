package com.example.project_csen_275;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

/**
 * Timer class for garden simulation that manages game time.
 * Converts real time to game time with 1 real minute = 1 game hour.
 */
public class GardenTimer {
    // Game time properties
    private IntegerProperty days = new SimpleIntegerProperty(1);
    private IntegerProperty hours = new SimpleIntegerProperty(6); // Start at 6 AM
    private IntegerProperty minutes = new SimpleIntegerProperty(0);
    private StringProperty timeString = new SimpleStringProperty("Day 1, 6:00 AM");

    // Session timer properties
    private long startTimeMillis;
    private IntegerProperty sessionMinutes = new SimpleIntegerProperty(0);
    private StringProperty sessionTimeString = new SimpleStringProperty("Session: 0h 0m");

    // Game time timeline
    private Timeline timeline;

    // Time multiplier: 1 real minute = 1 game hour
    private final int TIME_MULTIPLIER = 60;

    /**
     * Initialize the garden timer
     */
    public GardenTimer() {
        startTimeMillis = System.currentTimeMillis();

        // Setup timeline to update every second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> updateTime()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Start the timer
     */
    public void start() {
        timeline.play();
    }

    /**
     * Stop the timer
     */
    public void stop() {
        timeline.stop();
    }

    /**
     * Update game time and session time
     */
    private void updateTime() {
        // Update session time
        long elapsedSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000;
        int elapsedMinutes = (int) (elapsedSeconds / 60);
        int sessionHours = elapsedMinutes / 60;
        int sessionMins = elapsedMinutes % 60;
        sessionMinutes.set(elapsedMinutes);
        sessionTimeString.set(String.format("⏱️ Session: %dh %dm", sessionHours, sessionMins));

        // Update game time (1 real minute = 1 game hour)
        // Convert seconds to game minutes (1 real second = TIME_MULTIPLIER/60 game
        // minutes)
        int gameMinutesElapsed = (int) (elapsedSeconds * (TIME_MULTIPLIER / 60.0));
        int totalGameHours = gameMinutesElapsed / 60;
        int gameDays = totalGameHours / 24;
        int gameHours = (totalGameHours % 24);
        int gameMinutes = gameMinutesElapsed % 60;

        days.set(gameDays + 1); // Days start at 1

        // Apply 6AM offset and handle hour wrapping properly
        int adjustedHour = (gameHours + 6) % 24; // Start at 6AM (add 6 to the hour)
        hours.set(adjustedHour);
        minutes.set(gameMinutes);

        // Format the time string
        int displayHour = hours.get();
        String amPm = "AM";
        if (displayHour >= 12) {
            amPm = "PM";
            if (displayHour > 12) {
                displayHour -= 12;
            }
        }
        if (displayHour == 0) {
            displayHour = 12;
        }

        // Ensure time values are valid
        if (displayHour < 1)
            displayHour = 12;
        if (displayHour > 12)
            displayHour = displayHour % 12;
        if (displayHour == 0)
            displayHour = 12;

        timeString.set(String.format("🕒 Day %d, %d:%02d %s",
                days.get(), displayHour, minutes.get(), amPm));
    }

    /**
     * Get the formatted time string
     * 
     * @return The formatted time string
     */
    public StringProperty timeStringProperty() {
        return timeString;
    }

    /**
     * Get the session time string
     * 
     * @return The formatted session time string
     */
    public StringProperty sessionTimeStringProperty() {
        return sessionTimeString;
    }

    /**
     * Get the current day
     * 
     * @return The current day
     */
    public IntegerProperty daysProperty() {
        return days;
    }

    /**
     * Get the current hour
     * 
     * @return The current hour (0-23)
     */
    public IntegerProperty hoursProperty() {
        return hours;
    }

    /**
     * Get the current minute
     * 
     * @return The current minute (0-59)
     */
    public IntegerProperty minutesProperty() {
        return minutes;
    }

    /**
     * Get the session minutes elapsed
     * 
     * @return Minutes elapsed since the session started
     */
    public IntegerProperty sessionMinutesProperty() {
        return sessionMinutes;
    }
}
