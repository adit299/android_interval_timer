package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

public class TimerActivity extends AppCompatActivity {

    private Timer durationTimer;
    private Timer intervalTimer;
    private TextView durationVal;
    private TextView intervalTimingVal;
    private TextView numberOfBeepsVal;
    private Button startButton;
    private Button resetButton;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        // Extract initial timer values from saved instance state
        if (savedInstanceState != null) {
            // TODO: Get input values from saved instance state after they are set in main activity
        }
        // Get button elements
        backButton = (Button) findViewById(R.id.back_button);
        startButton = (Button) findViewById(R.id.start_timer_button);
        resetButton = (Button) findViewById(R.id.reset_timer_button);
        // Set button click behaviour
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBackToMain = new Intent(TimerActivity.this, MainActivity.class);
                TimerActivity.this.startActivity(goBackToMain);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        // Get text views
        durationVal = (TextView) findViewById(R.id.duration_val);
        intervalTimingVal = (TextView) findViewById(R.id.interval_timing_val);
        numberOfBeepsVal = (TextView) findViewById(R.id.number_of_beeps_val);
        // Initialize text values
        durationVal.setText("");
        intervalTimingVal.setText("");
        numberOfBeepsVal.setText("");
    }

    /**
     * Starts the timer including duration, interval timing, and number of beeps.
     */
    private void startTimer() {
        // TODO
    }

    /**
     * Resets the timer including duration, interval timing, and number of beeps.
     */
    private void resetTimer() {
        // Cancel the timers
        durationTimer.cancel();
        intervalTimer.cancel();
        // Reset text to the initial values
        durationVal.setText("");
        intervalTimingVal.setText("");
        numberOfBeepsVal.setText("");
    }
}