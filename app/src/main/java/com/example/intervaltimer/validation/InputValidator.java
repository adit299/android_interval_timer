package com.example.intervaltimer.validation;

import android.widget.Spinner;

public class InputValidator {

    private Integer fullDurationTimeSeconds;
    private Integer fullIntervalTimeSeconds;
    private Integer durationHours;
    private Integer durationMinutes;
    private Integer durationSeconds;
    private Integer intervalTimerHours;
    private Integer intervalTimerMinutes;
    private Integer intervalTimerSeconds;
    public Integer numOfBeeps;

    public InputValidator(String durationHours, String durationMinutes, String durationSeconds,
                          String intervalTimerHours, String intervalTimerMinutes,
                          String intervalTimerSeconds) {
        this.durationHours = Integer.valueOf(durationHours);
        this.durationMinutes = Integer.valueOf(durationMinutes);
        this.durationSeconds = Integer.valueOf(durationSeconds);
        this.intervalTimerHours = Integer.valueOf(intervalTimerHours);
        this.intervalTimerMinutes = Integer.valueOf(intervalTimerMinutes);
        this.intervalTimerSeconds = Integer.valueOf(intervalTimerSeconds);

        this.fullDurationTimeSeconds = (this.durationHours * 3600) + (this.durationMinutes*60) + this.durationSeconds;
        this.fullIntervalTimeSeconds = (this.intervalTimerHours * 3600) + (this.intervalTimerMinutes*60) + this.intervalTimerSeconds;

        System.out.println(fullDurationTimeSeconds);
        System.out.println(fullIntervalTimeSeconds);

        this.numOfBeeps = 0;
    }

    private Boolean isIntervalLessThanDuration() {
        return fullIntervalTimeSeconds <= fullDurationTimeSeconds;
    }

    private Boolean doesIntervalDivideIntoDuration() {
        boolean retVal = false;
        if(isIntervalLessThanDuration()) {
            retVal = (fullDurationTimeSeconds == 0 && fullIntervalTimeSeconds == 0) ? false : (fullDurationTimeSeconds % fullIntervalTimeSeconds) == 0;
        }
        return retVal;
    }


    public Boolean fullValidation() {
        return doesIntervalDivideIntoDuration();
    }

    public void setNumOfBeeps() {
        if(fullValidation()) {
            this.numOfBeeps = fullDurationTimeSeconds / fullIntervalTimeSeconds;
        }
    }

    public Integer getNumOfBeeps() { return this.numOfBeeps; }


}
