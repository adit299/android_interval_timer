package com.example.intervaltimer.timer;

public class TimerUtils {

    /**
     * Helper to format time from milliseconds into String.
     *
     * @param millis The time value in milliseconds.
     * @return The formatted time.
     */
    public static String formatTimeString(long millis) {
        int tenths = (int) (millis / 100) % 10;
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours = (int) ((millis / (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, tenths);
    }

    /**
     * Helper to format time from milliseconds into String.
     * Removes the tenth parameter
     * @param millis The time value in milliseconds.
     * @return The formatted time.
     */
    public static String formatTimeStringNoTenths(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours = (int) ((millis / (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    /**
     * Helper to format the number of beeps text
     *
     * @param finishedSets The number of beeps to display.
     * @return The formatted number of beeps.
     */
    public static String formatSets(int finishedSets, int totalSets) {
        return String.format("%d / %d", finishedSets, totalSets);
    }
}
