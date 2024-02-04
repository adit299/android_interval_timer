package com.example.intervaltimer;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class IntervalTimerApplication extends Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
