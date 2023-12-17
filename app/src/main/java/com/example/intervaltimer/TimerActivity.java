package com.example.intervaltimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class TimerActivity extends AppCompatActivity {

    /*** Temp hardcoded values for timer inputs ***/
    private static final long DURATION_MILLISECONDS = 10 * 1000; // 3 secs
    private static final long INTERVAL_MILLISECONDS = 2 * 1000; // 1 sec
    private static final int BEEPS = 5;
    /**********************************************/

    private static final String ZERO_TIME_STRING = formatTimeString(0);
    private static int beepCounter = 0;
    private String CHANNEL_ID = "1";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManager;
    private TextView durationVal;
    private TextView intervalTimingVal;
    private TextView numberOfBeepsVal;
    private Button startButton;
    private Button resetButton;
    private Button backButton;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Extract initial timer values from saved instance state
        // TODO

        // Initialize timer
        timer = createDurationTimer(DURATION_MILLISECONDS);

        // Initialize notification channel and notification settings
        createNotificationChannel();

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("A beep was triggered!!")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // Initialize Vibrator
        // vibrator = this.getSystemService(Vibrator.class);

        // Get button elements
        backButton = findViewById(R.id.back_button);
        startButton = findViewById(R.id.start_timer_button);
        resetButton = findViewById(R.id.reset_timer_button);

        // Set button click behaviour
        backButton.setOnClickListener(view ->
                TimerActivity.this.finish()
        );
        startButton.setOnClickListener(view ->
                timer.start()
        );
        resetButton.setOnClickListener(view -> {
            timer.cancel();
            resetTimerValues();
        });

        // Get text views
        durationVal = findViewById(R.id.duration_val);
        intervalTimingVal = findViewById(R.id.interval_timing_val);
        numberOfBeepsVal = findViewById(R.id.number_of_beeps_val);

        // Initialize text values
        durationVal.setText(formatTimeString(DURATION_MILLISECONDS));
        intervalTimingVal.setText(formatTimeString(INTERVAL_MILLISECONDS));
        numberOfBeepsVal.setText(formatNumberOfBeeps(0));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        // Maybe we don't need this check?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Customize the notification to vibrate and flash lights
            channel.setVibrationPattern(new long[]{0});
            channel.enableLights(true);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Initialize the timer that will update the UI every 100 milliseconds.
     *
     * @param durationMillis Duration value in milliseconds
     * @return The configured CountDownTimer
     */
    private CountDownTimer createDurationTimer(long durationMillis) {
        CountDownTimer durationTimer = new CountDownTimer(durationMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Sometimes this function gets called too quickly, avoid updating if no time has elapsed
                if (millisUntilFinished == DURATION_MILLISECONDS) {
                    return;
                }
                // Get value of interval timer, used to check if the interval is over
                String intervalTimeString = formatTimeString(millisUntilFinished % (INTERVAL_MILLISECONDS));
                // Check if the interval is done (Need to check string to avoid accuracy errors in durationMillis)
                if (intervalTimeString.equals(ZERO_TIME_STRING)) {
                    beepCounter += 1;
                    numberOfBeepsVal.setText(formatNumberOfBeeps(beepCounter));
                    // Make the phone produce a noise and vibrate once the interval is over
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    notificationManager.notify(new AtomicInteger().incrementAndGet(), notificationBuilder.build());
                }
                intervalTimingVal.setText(intervalTimeString);
                durationVal.setText(formatTimeString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                durationVal.setText(formatTimeString(0));
                intervalTimingVal.setText(formatTimeString(0));
            }
        };
        return durationTimer;
    }

    /**
     * Resets the timer
     */
    private void resetTimerValues() {
        // Reset text to the initial values
        durationVal.setText(formatTimeString(DURATION_MILLISECONDS));
        intervalTimingVal.setText(formatTimeString(INTERVAL_MILLISECONDS));
        numberOfBeepsVal.setText(formatNumberOfBeeps(0));
        beepCounter = 0;
    }

    /**
     * Helper to format time from milliseconds into String.
     *
     * @param millis The time value in milliseconds.
     * @return The formatted time.
     */
    private static String formatTimeString(long millis) {
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
    private static String formatNumberOfBeeps(int numberOfBeeps) {
        return String.format("%d / %d", numberOfBeeps, BEEPS);
    }
}