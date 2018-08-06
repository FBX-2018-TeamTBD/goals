package com.example.cassandrakane.goalz.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.example.cassandrakane.goalz.utils.DisplayUtil;

public class Emotion {

    static final int SMALL_SIZE = DisplayUtil.dpToPx(45);
    static final int MEDIUM_SIZE = DisplayUtil.dpToPx(60);
    static final int LARGE_SIZE = DisplayUtil.dpToPx(80);

    int size = 0;

    int startAnimatedSize;
    int endAnimatedSize;

    float x;
    float y;

    float startAnimatedX;

    float startAnimatedY;
    float endAnimatedY;

    private Paint textPaint;

    private Drawable imageDrawable;

    private Rect imageBound;
    private RectF textBound;

    private Context context;
    private float labelRatio;

    Emotion(Context context, Drawable drawable) {
        this.context = context;

        imageDrawable = drawable;

        textPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

        imageBound = new Rect();
        textBound = new RectF();

    }

    void draw(final Canvas canvas) {
        imageBound.set((int)x,(int) y, (int)x + size, (int)y + size);
        imageDrawable.setBounds(imageBound);
        imageDrawable.draw(canvas);
    }

    void setCurrentSize(int currentSize) {
        this.size = currentSize;
    }

}