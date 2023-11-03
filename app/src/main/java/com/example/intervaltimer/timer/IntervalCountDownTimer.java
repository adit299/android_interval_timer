package com.example.intervaltimer.timer;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.example.intervaltimer.R;

public class IntervalCountDownTimer {

    private CountDownTimer durationTimer;
    private CountDownTimer intervalTimer;

    public IntervalCountDownTimer(CountDownTimer durationTimer, CountDownTimer intervalTimer) {
        this.durationTimer = durationTimer;
        this.intervalTimer = intervalTimer;
    }

    public void startTimers() {
        this.durationTimer.start();
        this.intervalTimer.start();
    }

    public void cancelTimers() {
        this.durationTimer.cancel();
        this.intervalTimer.cancel();
    }

    public CountDownTimer getDurationTimer() {
        return durationTimer;
    }

    public void setDurationTimer(CountDownTimer durationTimer) {
        this.durationTimer = durationTimer;
    }

    public CountDownTimer getIntervalTimer() {
        return intervalTimer;
    }

    public void setIntervalTimer(CountDownTimer intervalTimer) {
        this.intervalTimer = intervalTimer;
    }
}
