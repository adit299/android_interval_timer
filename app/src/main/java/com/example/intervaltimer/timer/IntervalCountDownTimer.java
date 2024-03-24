package com.example.intervaltimer.timer;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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

    private NotificationCompat.Builder notificationBuilderProgress;

    private NotificationCompat.Builder notificationBuilderAlarm;

    private NotificationManagerCompat notificationManager;


    public IntervalCountDownTimer(TextView durationView,
                                  TextView intervalView,
                                  TextView setsView,
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
        int PROGRESS_MAX = 100;

        CountDownTimer durationTimer = new CountDownTimer(durationMillis, 100) {
            Boolean isFirstDurationNotification = true;
            @SuppressLint("MissingPermission")
            @Override
            public void onTick(long millisUntilFinished) {
                durationMillisUntilFinished = millisUntilFinished;
                intervalMillisUntilFinished = millisUntilFinished % totalIntervalMillis;
                durationView.setText(TimerUtils.formatTimeString(durationMillisUntilFinished));
                intervalView.setText(TimerUtils.formatTimeString(intervalMillisUntilFinished));

                int percentage = (int) ((1 - (double)(intervalMillisUntilFinished) / totalIntervalMillis) * 100);
                notificationBuilderProgress.setProgress(
                        PROGRESS_MAX,
                        percentage,
                        false
                );

                notificationBuilderProgress.setContentTitle(String.format("Sets: %s", TimerUtils.formatSets(setCounter, totalSets)));
                notificationBuilderProgress.setStyle(
                        new NotificationCompat.InboxStyle()
                                .addLine(String.format("Interval Duration: %s", TimerUtils.formatTimeStringNoTenths(millisUntilFinished % (totalIntervalMillis))))
                                .addLine(String.format("Total Duration: %s", TimerUtils.formatTimeStringNoTenths(millisUntilFinished)))
                );

                if(isFirstDurationNotification) {
                    notificationBuilderProgress.setOngoing(true);
                    isFirstDurationNotification = false;
                }

                notificationManager.notify(new AtomicInteger().incrementAndGet(), notificationBuilderProgress.build());

                durationView.setText(TimerUtils.formatTimeString(millisUntilFinished));
                intervalView.setText(TimerUtils.formatTimeString(millisUntilFinished % (totalIntervalMillis)));
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFinish() {
                durationView.setText(TimerUtils.formatTimeString(0));
                intervalView.setText(TimerUtils.formatTimeString(0));
                setsView.setText(TimerUtils.formatSets(totalSets, totalSets));
                durationMillisUntilFinished = 0;
                intervalMillisUntilFinished = 0;
                timerIsRunning = false;
                // Set progress bar in notification to be 100% full
                notificationBuilderProgress.setProgress(
                        PROGRESS_MAX,
                        PROGRESS_MAX,
                        false
                );
                notificationManager.notify(new AtomicInteger().incrementAndGet(), notificationBuilderProgress.build());
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
        int notificationId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
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
                notificationBuilderAlarm.setContentTitle(String.format("Set number %d has completed!!", setCounter - 1));
                notificationManager.notify(notificationId, notificationBuilderAlarm.build());
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFinish() {
                notificationBuilderAlarm.setContentTitle(String.format("Set number %d has completed!!", totalSets));
                notificationManager.notify(notificationId, notificationBuilderAlarm.build());
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
            int notificationId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            @Override
            public void onTick(long millisUntilFinished) {
                // Do nothing until finished
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFinish() {
                notificationBuilderAlarm.setContentTitle(String.format("Set number %d has completed!!", setCounter));
                notificationManager.notify(notificationId, notificationBuilderAlarm.build());
                intervalTimer = createIntervalTimer(durationMillisUntilFinished, totalIntervalMillis);
                intervalTimer.start();
            }
        };
        return resumeIntervalTimer;
    }
}
