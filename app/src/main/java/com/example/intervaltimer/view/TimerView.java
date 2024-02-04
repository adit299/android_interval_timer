package com.example.intervaltimer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import java.util.Vector;

public class TimerView extends View {
    private Paint paint;
    private int durationCircleRadius = getWidth() - (getWidth() / 10);
    private int screenCentreX = getWidth() / 2;
    private int screenCentreY = getHeight() / 2;
    private RectF durationCircle = new RectF(
            screenCentreX - durationCircleRadius,
            screenCentreY - durationCircleRadius,
            screenCentreX + durationCircleRadius,
            screenCentreY + durationCircleRadius
    );
    private boolean isInit = false;
    private long durationMillis;
    private long intervalMillis;
    private long totalDurationMillis;
    private long totalIntervalMillis;

    public TimerView(Context context) {
        super(context);
    }

    public void setTotalTime(long totalDuration, long totalInterval) {
        totalDurationMillis = totalDuration;
        totalIntervalMillis = totalInterval;
    }

    public void setElapsedTime(long duration, long interval) {
        durationMillis = duration;
        intervalMillis = interval;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInit) {
            init(canvas);
            isInit = true;
        }

        float durationAngle = convertTimeToAngle(durationMillis, totalDurationMillis);
        float intervalAngle = convertTimeToAngle(intervalMillis, totalIntervalMillis);

        drawDurationArc(canvas, durationAngle);
        drawIntervalHand(canvas, intervalAngle);
    }

    private void init(Canvas canvas) {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);

        canvas.drawLine(
                screenCentreX,
                screenCentreY,
                screenCentreX,
                screenCentreY - durationCircleRadius,
                paint
        );
    }

    private void drawDurationArc(Canvas canvas, float angle) {
        canvas.drawArc(durationCircle, -90, angle, true, paint);
    }

    private void drawIntervalHand(Canvas canvas, float angle) {
        float[] endCoords = rotateIntervalHand(angle);
        canvas.drawLine(
                screenCentreX,
                screenCentreY,
                endCoords[0],
                endCoords[1],
                paint
        );
    }

    private float[] rotateIntervalHand(float theta) {
        float rx = (float) ((screenCentreX * Math.cos(theta)) -
                (screenCentreY - durationCircleRadius * Math.sin(theta)));
        float ry = (float) ((screenCentreX * Math.sin(theta)) +
                (screenCentreY - durationCircleRadius * Math.cos(theta)));
        return new float[] { rx, ry };
    }

    private float convertTimeToAngle(long elapsedTime, long totalTime) {
        return 90f;
    }
}
