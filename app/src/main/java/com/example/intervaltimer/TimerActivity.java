package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.intervaltimer.timer.IntervalCountDownTimer;
import com.example.intervaltimer.timer.TimerButtonAction;
import com.example.intervaltimer.timer.TimerUtils;

public class TimerActivity extends AppCompatActivity {

    private TextView durationVal;
    private TextView intervalTimingVal;
    private TextView setsVal;
    private Button startButton;
    private Button resetButton;
    private Button backButton;
    private IntervalCountDownTimer timer;
    private long durationMillisInput;
    private long intervalMillisInput;
    private int setsInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Extract initial timer values from saved instance state
        Intent intent = getIntent();
        durationMillisInput = intent.getLongExtra(MainActivity.DURATION_INTENT_KEY, 0);
        intervalMillisInput = intent.getLongExtra(MainActivity.INTERVAL_INTENT_KEY, 0);
        setsInput = intent.getIntExtra(MainActivity.BEEPS_INTENT_KEY, 0);

        // Get text views
        durationVal = findViewById(R.id.duration_val);
        intervalTimingVal = findViewById(R.id.interval_timing_val);
        setsVal = findViewById(R.id.number_of_beeps_val);

        // Initialize timer
        timer = new IntervalCountDownTimer(
                durationVal,
                intervalTimingVal,
                setsVal,
                durationMillisInput,
                intervalMillisInput,
                setsInput
        );

        // Initialize text view values
        durationVal.setText(TimerUtils.formatTimeString(durationMillisInput));
        intervalTimingVal.setText(TimerUtils.formatTimeString(intervalMillisInput));
        setsVal.setText(TimerUtils.formatSets(0, setsInput));

        // Get button elements
        backButton = findViewById(R.id.back_button);
        startButton = findViewById(R.id.start_timer_button);
        resetButton = findViewById(R.id.reset_timer_button);

        // Set button click behaviour
        backButton.setOnClickListener(view -> {
            processButtonPress(TimerButtonAction.BACK);
        });
        startButton.setOnClickListener(view -> {
            if (timer.isRunning()) {
                processButtonPress(TimerButtonAction.PAUSE);
            } else if (timer.getRemainingDuration() > 0
                    && timer.getRemainingDuration() < timer.getTotalDuration()) {
                processButtonPress(TimerButtonAction.RESUME);
            } else if (timer.getRemainingDuration() == timer.getTotalDuration()) {
                processButtonPress(TimerButtonAction.START);
            } else {
                // Timer is done, do nothing
            }
        });
        resetButton.setOnClickListener(view -> {
            processButtonPress(TimerButtonAction.RESET);
        });
    }

    private void processButtonPress(TimerButtonAction action) {
        switch (action) {
            case START:
                timer.startTimer();
                startButton.setText(TimerButtonAction.PAUSE.text);
                break;
            case PAUSE:
                timer.pauseTimer();
                startButton.setText(TimerButtonAction.RESUME.text);
                break;
            case RESUME:
                timer.resumeTimer();
                startButton.setText(TimerButtonAction.PAUSE.text);
                break;
            case RESET:
                timer.resetTimer();
                startButton.setEnabled(true);
                startButton.setText(TimerButtonAction.START.text);
                break;
            case BACK:
                timer.cancelTimer();
                TimerActivity.this.finish();
                break;
        }
    }
}