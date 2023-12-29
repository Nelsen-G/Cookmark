package com.example.cookmark_app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.CalendarView;

import java.util.Calendar;

public class CustomCalendarView extends CalendarView {

    private Paint textPaint = new Paint();
    private Paint backgroundPaint = new Paint();

    public CustomCalendarView(Context context) {
        super(context);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textPaint.setColor(Color.WHITE);
        backgroundPaint.setColor(Color.parseColor("#800080")); // Replace with your desired purple color
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Iterate over the days to find and color the past dates
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getDate());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        while (calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
            if (calendar.before(today)) {
                // Draw a grey rectangle for past dates
                drawGreyBackground(canvas, calendar);
            } else {
                // Draw white text for current month's dates
                drawWhiteText(canvas, calendar);
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void drawGreyBackground(Canvas canvas, Calendar calendar) {
        long dayMillis = 24 * 60 * 60 * 1000;
        int cellWidth = getWidth() / 7;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int x = (dayOfMonth - 1) % 7 * cellWidth;
        int y = (dayOfMonth - 1) / 7 * cellWidth;
        int cellHeight = getHeight() / 6;

        canvas.drawRect(x, y, x + cellWidth, y + cellHeight, backgroundPaint);
    }

    private void drawWhiteText(Canvas canvas, Calendar calendar) {
        int cellWidth = getWidth() / 7;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int x = (dayOfMonth - 1) % 7 * cellWidth + cellWidth / 2;
        int y = (dayOfMonth - 1) / 7 * cellWidth + cellWidth / 2;

        String dayText = String.valueOf(dayOfMonth);
        float textWidth = textPaint.measureText(dayText);
        float textX = x - textWidth / 2;
        float textY = y - (textPaint.descent() + textPaint.ascent()) / 2;

        canvas.drawText(dayText, textX, textY, textPaint);
    }
}
