package com.erminesoft.motionview.motionview.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CircularProgress extends View {

    private static final String TAG = "CUSTOM_VIEW";
    private static final float UNUSED_PART_START = 45;
    private static final float UNUSED_PART_SWEEP = 90;

    private static final float AVAILABLE_ANGLE = 360 - UNUSED_PART_SWEEP;

    private static final float DIVIDER_PART_SWEEP = 2;

    private RectF oval;
    private float diameter;

    private Paint unusedPart;
    private Paint emptyPart;

    private float unusedEndAngel;
    private int strokeWidth;

    private List<Part> parts;
    private long totalTime;

    private int currentProgress;
    private int maxProgress;
    private float percentageProgress;
    private Paint dividerPaint;
    private Paint percentageText;

    private float textSize;

    public CircularProgress(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = dpToPx(275) + getPaddingLeft() + getPaddingRight();
        int desiredHeight = dpToPx(275) + getPaddingTop() + getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int w = 0;
        int h = 0;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                w = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                w = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.UNSPECIFIED:
                w = desiredWidth;
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                h = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                h = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.UNSPECIFIED:
                h = desiredHeight;
                break;
        }

        strokeWidth = w / 10;

        int paddingOffset = strokeWidth / 2;

        int paddingTop = getPaddingTop() + paddingOffset;
        int paddingBottom = getPaddingBottom() + paddingOffset;
        int paddingLeft = getPaddingLeft() + paddingOffset;
        int paddingRight = getPaddingRight() + paddingOffset;

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int ww = w - getPaddingRight();
        int hh = h - getPaddingBottom();

        diameter = Math.min(ww, hh);

        textSize = (diameter / 2) * 0.25f;

        oval.set(getPaddingLeft(), getPaddingTop(), diameter, diameter);
        unusedPart.setStrokeWidth(strokeWidth);
        emptyPart.setStrokeWidth(strokeWidth);
        dividerPaint.setStrokeWidth(strokeWidth);
    }

    private void init() {
        parts = new ArrayList<>();

        oval = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth(), getMeasuredHeight());

        unusedEndAngel = UNUSED_PART_START + UNUSED_PART_SWEEP;

        unusedPart = new Paint(Paint.ANTI_ALIAS_FLAG);
        unusedPart.setColor(Color.WHITE);
        unusedPart.setStyle(Paint.Style.STROKE);

        emptyPart = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPart.setColor(Color.GRAY);
        emptyPart.setStyle(Paint.Style.STROKE);

        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(Color.WHITE);
        dividerPaint.setStyle(Paint.Style.STROKE);

        percentageText = new Paint(Paint.LINEAR_TEXT_FLAG);
        percentageText.setColor(Color.DKGRAY);
        percentageText.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(oval,
                UNUSED_PART_START, UNUSED_PART_SWEEP, false, unusedPart);

        float availableAngle = (AVAILABLE_ANGLE - parts.size() * DIVIDER_PART_SWEEP) * percentageProgress;

        if (availableAngle == 0) {
            canvas.drawArc(oval, unusedEndAngel, AVAILABLE_ANGLE, false, emptyPart);
        } else {

            float startAngle;
            float sweep;
            float endAngle = unusedEndAngel;

            float partPercentage;
            Paint paint;

            for (Part part : parts) {
                partPercentage = (float) part.getTime() / totalTime;

                startAngle = endAngle;
                sweep = availableAngle * partPercentage;

                paint = part.getPaint();
                paint.setStrokeWidth(strokeWidth);
                canvas.drawArc(oval, startAngle, sweep, false, paint);

                endAngle = startAngle + sweep;
                canvas.drawArc(oval, endAngle, DIVIDER_PART_SWEEP, false, dividerPaint);

                endAngle += DIVIDER_PART_SWEEP;
            }

            canvas.drawArc(oval, endAngle, 360 + UNUSED_PART_START - endAngle, false, emptyPart);
        }

        float percentageTextX = diameter / 2 + diameter * 0.06f;
        float percentageTextY = diameter / 2;

        percentageText.setTextSize(textSize);


        canvas.drawText(String.format(Locale.getDefault(), "%.1f%%", percentageProgress * 100),
                percentageTextX, percentageTextY,
                percentageText);

        canvas.drawText(currentProgress + "/" + maxProgress, percentageTextX,
                percentageTextY + percentageText.getTextSize(), percentageText);
    }

    public void addPart(@NonNull Part part) {
        parts.add(part);

        totalTime += part.getTime();
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;

        reInitValues();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;

        reInitValues();
    }

    private void reInitValues() {
        if (maxProgress < currentProgress) {
            percentageProgress = 1.0f;
        } else if (maxProgress == 0 || currentProgress == 0) {
            percentageProgress = 0;
        } else {
            percentageProgress = (float) currentProgress / maxProgress;
        }
    }

    public void clear() {
        parts.clear();
        totalTime = 0;
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static class Part {
        private Paint paint;
        private long activityTime;

        public Part(long activityTime, int color) {
            this.activityTime = activityTime;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
        }

        public Paint getPaint() {
            return paint;
        }

        public long getTime() {
            return activityTime;
        }
    }
}