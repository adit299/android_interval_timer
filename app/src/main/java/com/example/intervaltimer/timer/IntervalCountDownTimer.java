package com.example.intervaltimer.timer;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class IntervalCountDownTimer {

    private static final int PROGRESS_MAX = 100;
    private static final float FULL_ROTATION = 360f;
    // Helper values
    private int setCounter;
    private boolean timerIsRunning;

    // UI values
    private final TextView durationView;
    private final TextView intervalView;
    private final TextView setsView;
    private final CircularProgressIndicator intervalProgressIndicator;
    private final CircularProgressIndicator durationProgressIndicator;
    private long totalDurationMillis;
    private long totalIntervalMillis;
    private long durationMillisUntilFinished;
    private long intervalMillisUntilFinished;
    private int totalSets;

    // Timers
    private CountDownTimer durationTimer;
    private CountDownTimer intervalTimer;

    private NotificationCompat.Builder notificationBuilderProgress;

    private NotificationCompat.Builder notificationBuilderAlarm;

    private NotificationManagerCompat notificationManager;


    public IntervalCountDownTimer(TextView durationView,
                                  TextView intervalView,
                                  TextView setsView,
                                  CircularProgressIndicator intervalProgressIndicator,
                                  CircularProgressIndicator durationProgressIndicator,
                                  long durationMillis,
                                  long intervalMillis,
                                  int totalSets, NotificationCompat.Builder notificationBuilderProgress,
                                  NotificationCompat.Builder notificationBuilderAlarm,
                                  NotificationManagerCompat notificationManager) {
        this.setCounter = 0;
        this.timerIsRunning = false;
        this.totalSets = totalSets;
        this.durationView = durationView;
        this.intervalView = intervalView;
        this.setsView = setsView;
        this.intervalProgressIndicator = intervalProgressIndicator;
        this.durationProgressIndicator = durationProgressIndicator;
        this.totalDurationMillis = durationMillis;
        this.totalIntervalMillis = intervalMillis;
        this.durationMillisUntilFinished = durationMillis;
        this.intervalMillisUntilFinished = intervalMillis;
        this.durationTimer = createDurationTimer(durationMillis);
        this.intervalTimer = createIntervalTimer(durationMillis, intervalMillis);
        this.notificationBuilderProgress = notificationBuilderProgress;
        this.notificationBuilderAlarm = notificationBuilderAlarm;
        this.notificationManager = notificationManager;
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
        // Rest progress bar
        intervalProgressIndicator.setRotation(0);
        durationProgressIndicator.setProgress(0);
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
            Boolean isFirstDurationNotification = true;
            @SuppressLint("MissingPermission")
            @Override
            public void onTick(long millisUntilFinished) {
                durationMillisUntilFinished = millisUntilFinished;
                intervalMillisUntilFinished = millisUntilFinished % totalIntervalMillis;
                durationView.setText(TimerUtils.formatTimeString(durationMillisUntilFinished));
                intervalView.setText(TimerUtils.formatTimeString(intervalMillisUntilFinished));

                int intervalPercentage = (int) ((1 - (double)(intervalMillisUntilFinished) / totalIntervalMillis) * 100);
                int durationProgress = (int)((totalDurationMillis - durationMillisUntilFinished) / 100);
                float intervalPercentageFloat = ((1 - (float)(intervalMillisUntilFinished) / totalIntervalMillis) * 100f);
                float intervalProgressDegrees = (intervalPercentageFloat / 100f) * FULL_ROTATION;

                notificationBuilderProgress.setProgress(
                        PROGRESS_MAX,
                        intervalPercentage,
                        false
                );
                notificationManager.notify(new AtomicInteger().incrementAndGet(), notificationBuilderProgress.build());
                if(isFirstDurationNotification) {
                    notificationBuilderProgress.setOngoing(true);
                    isFirstDurationNotification = false;
                }
                durationView.setText(TimerUtils.formatTimeString(millisUntilFinished));
                intervalView.setText(TimerUtils.formatTimeString(millisUntilFinished % (totalIntervalMillis)));
                intervalProgressIndicator.setRotation(intervalProgressDegrees);
                durationProgressIndicator.setProgress(durationProgress);
            }

            @Override
            public void onFinish() {
                durationView.setText(TimerUtils.formatTimeString(0));
                intervalView.setText(TimerUtils.formatTimeString(0));
                setsView.setText(TimerUtils.formatSets(totalSets, totalSets));
                notificationBuilderProgress.setProgress(
                        PROGRESS_MAX,
                        PROGRESS_MAX,
                        false
                );
                intervalProgressIndicator.setRotation(0);
                durationProgressIndicator.setProgress(PROGRESS_MAX * 10);
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
            Boolean isFirstIntervalNotification = true;
            @SuppressLint("MissingPermission")
            @Override
            public void onTick(long millisUntilFinished) {
                setsView.setText(TimerUtils.formatSets(setCounter, totalSets));
                setCounter += 1;

                if(isFirstIntervalNotification) {
                    isFirstIntervalNotification = false;
                    return;
                }
                notificationManager.notify(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                        notificationBuilderAlarm.build());
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFinish() {
                notificationManager.notify(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE),
                        notificationBuilderAlarm.build());
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
