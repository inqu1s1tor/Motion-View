package com.erminesoft.motionview.motionview.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.erminesoft.motionview.motionview.util.TypeFaceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CircularProgress extends View {
    private static final float AVAILABLE_ANGLE = 360;
    private static final float DIVIDER_PART_SWEEP = 2;
    private static final float START_ANGLE = 90;
    private static final float ANIMATION_TIME = 15;
    private static final String HELVETICA_ROMAN = "fonts/HELVETICANEUECYR-ROMAN.OTF";
    private static final String ROBOTO_REGULAR = "fonts/ROBOTO-REGULAR_0.TTF";

    private RectF oval;
    private float diameter;

    private Paint unusedPart;
    private Paint emptyPart;

    private int strokeWidth;

    private List<Part> parts;
    private long totalTime;

    private int currentProgress;
    private int maxProgress;
    private float percentageProgress;
    private Paint dividerPaint;

    private Paint percentageText;
    private Paint blackText;

    private float textSize;

    private boolean isAnimating;
    private int lastPartIndex;
    private float animationStartAngle;
    private float animationSweep;
    private float animationTime;
    private float delta = -1f;


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
                if (w > h) {
                    h = w;
                } else {
                    h = desiredHeight;
                }
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

        textSize = (diameter / 2) * 0.52f;

        oval.set(getPaddingLeft(), getPaddingTop(), diameter, diameter);
        unusedPart.setStrokeWidth(strokeWidth);
        emptyPart.setStrokeWidth(strokeWidth);
        dividerPaint.setStrokeWidth(strokeWidth);
    }

    private void init() {
        parts = new ArrayList<>();

        oval = new RectF(getPaddingLeft(), getPaddingTop(), getMeasuredWidth(), getMeasuredHeight());

        unusedPart = new Paint(Paint.ANTI_ALIAS_FLAG);
        unusedPart.setColor(Color.WHITE);
        unusedPart.setStyle(Paint.Style.STROKE);

        emptyPart = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPart.setColor(Color.parseColor("#a9a9a9"));
        emptyPart.setStyle(Paint.Style.STROKE);

        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(Color.parseColor("#eeeeee"));
        dividerPaint.setStyle(Paint.Style.STROKE);

        percentageText = new Paint(Paint.LINEAR_TEXT_FLAG);
        percentageText.setColor(Color.parseColor("#ABABAB"));
        percentageText.setTextAlign(Paint.Align.CENTER);
        percentageText.setShadowLayer(2f, -1f, -1f, Color.BLACK);
        percentageText.setTypeface(TypeFaceHelper.getInstance().getTypeFace(HELVETICA_ROMAN));

        setLayerType(LAYER_TYPE_SOFTWARE, percentageText);

        blackText = new Paint(Paint.LINEAR_TEXT_FLAG);
        blackText.setTextAlign(Paint.Align.CENTER);
        blackText.setTypeface(TypeFaceHelper.getInstance().getTypeFace(ROBOTO_REGULAR));
        blackText.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startAngle;
        float sweep;
        float endAngle = START_ANGLE;

        canvas.drawArc(oval, START_ANGLE, AVAILABLE_ANGLE, false, emptyPart);

        float percentageTextX = diameter / 2 + diameter * 0.055f;
        float percentageTextY = diameter / 2 - 10;

        percentageText.setTextSize(textSize);

        canvas.drawText(String.format(Locale.getDefault(), "%.0f%%", percentageProgress * 100),
                percentageTextX, percentageTextY * 1.2f,
                percentageText);

        blackText.setTextSize(textSize / 3.5f);

        canvas.drawText("ACTIVITY SCORE",
                percentageTextX,
                percentageTextY + percentageText.getTextSize(),
                blackText);

        blackText.setTextSize(textSize / 2.8f);

        canvas.drawText(currentProgress + "/" + maxProgress, percentageTextX,
                percentageTextY + percentageText.getTextSize() + blackText.getTextSize(), blackText);

        float availableAngle = (AVAILABLE_ANGLE - parts.size() * DIVIDER_PART_SWEEP) * percentageProgress;

        float partPercentage;
        Paint paint;

        for (int i = 0; i < parts.size(); i++) {
            canvas.drawArc(oval, endAngle, DIVIDER_PART_SWEEP, false, dividerPaint);
            endAngle += DIVIDER_PART_SWEEP;

            if (isAnimating && lastPartIndex == i) {
                break;
            }

            Part part = parts.get(i);

            partPercentage = (float) part.getTime() / totalTime;

            startAngle = endAngle;
            sweep = availableAngle * partPercentage;

            paint = part.getPaint();
            paint.setStrokeWidth(strokeWidth);
            endAngle = startAngle + sweep;

            if (!isAnimating && lastPartIndex == i) {
                animatePart(startAngle, sweep, endAngle, i);
                break;
            } else {
                canvas.drawArc(oval, startAngle, sweep, false, paint);
            }
        }

        if (!isAnimating) {
            canvas.drawArc(oval, endAngle, DIVIDER_PART_SWEEP, false, dividerPaint);
        }

        if (isAnimating) {
            canvas.drawArc(oval, animationStartAngle, animationSweep, false, parts.get(lastPartIndex).getPaint());
        }

    }

    private void animatePart(final float startAngle, final float sweep, final float endAngle, final int index) {
        isAnimating = true;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                animationTime += 1;
                lastPartIndex = index;
                animationStartAngle = startAngle;

                if (delta == -1) {
                    delta = sweep / ANIMATION_TIME;
                }

                animationSweep += delta;

                invalidate();
                if (animationTime < ANIMATION_TIME && startAngle < endAngle) {
                    animatePart(startAngle, animationSweep, endAngle, index);
                } else {
                    isAnimating = false;
                    animationTime = 0;
                    animationSweep = 0;
                    animationStartAngle = 0;
                    lastPartIndex++;
                    delta = -1f;
                }
            }
        });
    }

    public void addParts(Part[] p) {
        parts = new ArrayList<>(Arrays.asList(p));

        for (int i = 0; i < parts.size(); i++) {
            Part part = parts.get(i);
            if (part == null) {
                parts.remove(i--);
                continue;
            }

            totalTime += part.getTime();
        }
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
