package com.project.flyingchess.widget.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by wzm on 2016/3/26.
 */
public class Circle {
    float x, y;
    float radius = 16;
    Paint paint;
    Paint paint_edge;

    public Circle() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        paint_edge = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_edge.setAntiAlias(true);
        paint_edge.setColor(Color.BLACK);
        paint_edge.setStyle(Paint.Style.STROKE);
        paint_edge.setStrokeWidth(1);
    }

    public void setPara(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return 20;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public void drawEdge(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint_edge);
    }
}
