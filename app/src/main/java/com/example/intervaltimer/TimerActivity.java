package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import com.example.intervaltimer.timer.IntervalCountDownTimer;

public class TimerActivity extends AppCompatActivity {

    private int beepCounter;
    private TextView durationVal;
    private TextView intervalTimingVal;
    private TextView numberOfBeepsVal;
    private Button startButton;
    private Button resetButton;
    private Button backButton;
    private IntervalCountDownTimer timer;
    private long durationMillisInput;
    private long intervalMillisInput;
    private int numOfBeepsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        beepCounter = 0;

        // Extract initial timer values from saved instance state
        Intent intent = getIntent();
        durationMillisInput = intent.getLongExtra(MainActivity.DURATION_INTENT_KEY, 0);
        intervalMillisInput = intent.getLongExtra(MainActivity.INTERVAL_INTENT_KEY, 0);
        numOfBeepsInput = intent.getIntExtra(MainActivity.BEEPS_INTENT_KEY, 0);

        // Initialize timer
        timer = new IntervalCountDownTimer(
            createDurationTimer(durationMillisInput, 100),
            createIntervalTimer(durationMillisInput, intervalMillisInput)
        );

        // Get button elements
        backButton = findViewById(R.id.back_button);
        startButton = findViewById(R.id.start_timer_button);
        resetButton = findViewById(R.id.reset_timer_button);

        // Set button click behaviour
        backButton.setOnClickListener(view -> {
            timer.cancelTimers();
            TimerActivity.this.finish();
        });
        startButton.setOnClickListener(view ->
            timer.startTimers()
        );
        resetButton.setOnClickListener(view -> {
            timer.cancelTimers();
            resetTimerValues();
        });

        // Get text views
        durationVal = findViewById(R.id.duration_val);
        intervalTimingVal = findViewById(R.id.interval_timing_val);
        numberOfBeepsVal = findViewById(R.id.number_of_beeps_val);

        // Initialize text values
        durationVal.setText(formatTimeString(durationMillisInput));
        intervalTimingVal.setText(formatTimeString(intervalMillisInput));
        numberOfBeepsVal.setText(formatNumberOfBeeps(0));
    }

    /**
     * Initialize the CountDownTimer for the duration timer field.
     *
     * @param durationMillis Duration value in milliseconds
     * @param intervalMillis Interval value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createDurationTimer(long durationMillis, long intervalMillis) {
        CountDownTimer durationTimer = new CountDownTimer(durationMillis, intervalMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                durationVal.setText(formatTimeString(millisUntilFinished));
                intervalTimingVal.setText(formatTimeString(millisUntilFinished % (intervalMillisInput)));
            }

            @Override
            public void onFinish() {
                durationVal.setText(formatTimeString(0));
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
                numberOfBeepsVal.setText(formatNumberOfBeeps(beepCounter));
                beepCounter += 1;
            }

            @Override
            public void onFinish() {
                intervalTimingVal.setText(formatTimeString(0));
                numberOfBeepsVal.setText(formatNumberOfBeeps(beepCounter));
            }
        };
        return intervalTimer;
    }

    /**
     * Resets the timer
     */
    private void resetTimerValues() {
        // Reset text to the initial values
        durationVal.setText(formatTimeString(durationMillisInput));
        intervalTimingVal.setText(formatTimeString(intervalMillisInput));
        numberOfBeepsVal.setText(formatNumberOfBeeps(0));
        beepCounter = 0;
    }

    /**
     * Helper to format time from milliseconds into String.
     *
     * @param millis The time value in milliseconds.
     * @return The formatted time.
     */
    private String formatTimeString(long millis) {
        int tenths = (int) (millis / 100) % 10;
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours = (int) ((millis / (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d.%d", hours, minutes, seconds, tenths);
    }

    /**
     * Helper to format the number of beeps text
     *
     * @param numberOfBeeps The number of beeps to display.
     * @return The formatted number of beeps.
     */
    private String formatNumberOfBeeps(int numberOfBeeps) {
        return String.format("%d / %d", numberOfBeeps, numOfBeepsInput);
    }
}