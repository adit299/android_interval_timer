package com.example.intervaltimer.validation;

import android.widget.Spinner;

public class InputValidator {

    private String fullDurationTime;
    private String fullIntervalTime;
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

        this.fullDurationTime = durationHours + durationMinutes + durationSeconds;
        this.fullIntervalTime = intervalTimerHours + intervalTimerMinutes + intervalTimerSeconds;

        this.numOfBeeps = 0;
    }

    private Boolean isIntervalLessThanDuration() {
        return intervalTimerHours <= durationHours &&
                intervalTimerMinutes <= durationMinutes &&
                intervalTimerSeconds <= durationSeconds;
    }

    private Boolean doesIntervalDivideIntoDuration() {
        boolean retVal = false;
        if(isIntervalLessThanDuration()) {
            retVal = (Integer.valueOf(fullDurationTime) % Integer.valueOf(fullIntervalTime)) == 0;
        }
        if(retVal) {
            this.numOfBeeps = Integer.valueOf(fullDurationTime) / Integer.valueOf(fullIntervalTime);
        }
        return retVal;
    }


    public Boolean fullValidation() {
        return doesIntervalDivideIntoDuration();
    }




}
