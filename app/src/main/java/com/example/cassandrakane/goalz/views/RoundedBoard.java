package com.example.cassandrakane.goalz.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.example.cassandrakane.goalz.utils.Constants;
import com.example.cassandrakane.goalz.utils.DisplayUtil;

public class RoundedBoard {

    static final int WIDTH = 5 * Emotion.MEDIUM_SIZE + 6 * Constants.HORIZONTAL_SPACING;

    static final int HEIGHT = DisplayUtil.dpToPx(85);

    static final int SCALED_DOWN_HEIGHT = DisplayUtil.dpToPx(70);

    static final float LEFT = DisplayUtil.dpToPx(16);

    static final float BOTTOM = DisplayUtil.dpToPx(780);

    static final float TOP = BOTTOM - HEIGHT;

    static final float BASE_LINE = TOP + Emotion.MEDIUM_SIZE + Constants.VERTICAL_SPACING;

    float height = HEIGHT;
    float y;

    private float radius = height / 2;

    float startAnimatedHeight;
    float endAnimatedHeight;

    float startAnimatedY;
    float endAnimatedY;

    private Paint boardPaint;
    private RectF rect;

    RoundedBoard() {
        initPaint();
        rect = new RectF();
    }

    private void initPaint() {
        boardPaint = new Paint();
        boardPaint.setAntiAlias(true);
        boardPaint.setStyle(Paint.Style.FILL);
        boardPaint.setColor(Color.WHITE);
        boardPaint.setShadowLayer(5.0f, 0.0f, 2.0f, 0xFF000000);
    }

    void setCurrentHeight(float newHeight) {
        height = newHeight;
        y = BOTTOM - height;
    }

    void draw(Canvas canvas) {
        rect.set(LEFT, y, LEFT + WIDTH, BOTTOM);
        canvas.drawRoundRect(rect, radius, radius, boardPaint);
    }

}