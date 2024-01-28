package com.example.intervaltimer.timer;

import android.os.CountDownTimer;
import android.widget.TextView;

public class IntervalCountDownTimer {

    // Helper values
    private int setCounter;
    private boolean timerIsRunning;

    // UI values
    private TextView durationView;
    private TextView intervalView;
    private TextView setsView;
    private long totalDurationMillis;
    private long totalIntervalMillis;
    private long durationMillisUntilFinished;
    private long intervalMillisUntilFinished;
    private int totalSets;

    // Timers
    private CountDownTimer durationTimer;
    private CountDownTimer intervalTimer;

    public IntervalCountDownTimer(TextView durationView,
                                  TextView intervalView,
                                  TextView setsView,
                                  long durationMillis,
                                  long intervalMillis,
                                  int totalSets) {
        this.setCounter = 0;
        this.timerIsRunning = false;
        this.totalSets = totalSets;
        this.durationView = durationView;
        this.intervalView = intervalView;
        this.setsView = setsView;
        this.totalDurationMillis = durationMillis;
        this.totalIntervalMillis = intervalMillis;
        this.durationMillisUntilFinished = durationMillis;
        this.intervalMillisUntilFinished = intervalMillis;
        this.durationTimer = createDurationTimer(durationMillis);
        this.intervalTimer = createIntervalTimer(durationMillis, intervalMillis);
    }

    public boolean isRunning() {
        return timerIsRunning;
    }

    public void startTimer() {
        durationTimer.start();
        intervalTimer.start();
        timerIsRunning = true;
    }

    public void cancelTimer() {
        durationTimer.cancel();
        intervalTimer.cancel();
        timerIsRunning = false;
    }

    public void pauseTimer() {
        cancelTimer();
    }

    public void resumeTimer() {
        // Crete new timers with remaining times
        durationTimer = createDurationTimer(durationMillisUntilFinished);
        intervalTimer = createResumeIntervalTimer(intervalMillisUntilFinished);
        startTimer();
    }

    public void resetTimer() {
        cancelTimer();
        // Create new timers with initial input values
        durationTimer = createDurationTimer(totalDurationMillis);
        intervalTimer = createIntervalTimer(totalDurationMillis, totalIntervalMillis);
        setCounter = 0;
        durationMillisUntilFinished = totalDurationMillis;
        intervalMillisUntilFinished = totalIntervalMillis;
        // Reset UI test to initial values
        durationView.setText(TimerUtils.formatTimeString(totalDurationMillis));
        intervalView.setText(TimerUtils.formatTimeString(totalIntervalMillis));
        setsView.setText(TimerUtils.formatSets(setCounter, totalSets));
    }

    public long getRemainingDuration() {
        return durationMillisUntilFinished;
    }

    public long getTotalDuration() {
        return totalDurationMillis;
    }

    /**
     * Initialize the CountDownTimer for the duration timer field.
     *
     * @param durationMillis Duration value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createDurationTimer(long durationMillis) {
        CountDownTimer durationTimer = new CountDownTimer(durationMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                durationMillisUntilFinished = millisUntilFinished;
                intervalMillisUntilFinished = millisUntilFinished % totalIntervalMillis;
                durationView.setText(TimerUtils.formatTimeString(durationMillisUntilFinished));
                intervalView.setText(TimerUtils.formatTimeString(intervalMillisUntilFinished));
            }

            @Override
            public void onFinish() {
                durationView.setText(TimerUtils.formatTimeString(0));
                intervalView.setText(TimerUtils.formatTimeString(0));
                setsView.setText(TimerUtils.formatSets(totalSets, totalSets));
                durationMillisUntilFinished = 0;
                intervalMillisUntilFinished = 0;
                timerIsRunning = false;
            }
        };
        return durationTimer;
    }

    /**
     * Initialize the CountDownTimer for the interval field.
     *
     * @param durationMillis Duration value in milliseconds
     * @param intervalMillis Interval value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createIntervalTimer(long durationMillis, long intervalMillis) {
        CountDownTimer intervalTimer = new CountDownTimer(durationMillis, intervalMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                setsView.setText(TimerUtils.formatSets(setCounter, totalSets));
                setCounter += 1;
            }

            @Override
            public void onFinish() {
                // Do Nothing
            }
        };
        return intervalTimer;
    }

    /**
     * Initialize the CountDownTimer for the remainder of interval after pause.
     * Restarts the standard interval timer when finished.
     *
     * @param intervalTimeRemaining Interval value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createResumeIntervalTimer(long intervalTimeRemaining) {
        CountDownTimer resumeIntervalTimer = new CountDownTimer(intervalTimeRemaining, intervalTimeRemaining) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing until finished
            }

            @Override
            public void onFinish() {
                intervalTimer = createIntervalTimer(durationMillisUntilFinished, totalIntervalMillis);
                intervalTimer.start();
            }
        };
        return resumeIntervalTimer;
    }
}
