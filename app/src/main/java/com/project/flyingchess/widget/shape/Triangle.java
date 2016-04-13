package com.project.flyingchess.widget.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.project.flyingchess.widget.ChessBoard;

/**
 * Created by wzm on 2016/3/26.
 */
public class Triangle extends MyShape {

    public float x1, y1, x2, y2, x3, y3;
    //public int num;
    Paint paint;

    public Triangle() {
        super();
    }

    @Override
    public boolean isPointInRegion(float x, float y) {
        double triangleArea = triangleArea(x1,y1,x2,y2,x3,y3);
        double area = triangleArea(x,y,x1,y1,x2,y2);
        area += triangleArea(x,y,x1,y1,x3,y3);
        area += triangleArea(x,y,x2,y2,x3,y3);
        double epsilon = 1;  // 由于浮点数的计算存在着误差，故指定一个足够小的数，用于判定两个面积是否(近似)相等。
        if (Math.abs(triangleArea - area) < epsilon) {
            return true;
        }
        return false;
    }

    private double triangleArea(float x1,float y1,float x2,float y2,float x3,float y3) {
        double result = Math.abs((x1 * y2 + x2 * y3 + x3 * y1
                - x2 * y1 - x3 * y2 - x1 * y3) / 2.0D);
        return result;
    }

    public void setCoord(float x1, float y1, float x2, float y2, float x3, float y3, int num, Paint paint) {
        this.x1  = x1;
        this.y1  = y1;
        this.x2  = x2;
        this.y2  = y2;
        this.x3  = x3;
        this.y3  = y3;
        this.num = num;
        this.paint = paint;
        ChessBoard.planePosition.put(num, this);
    }

    public void draw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        //绘制三角形
        canvas.drawPath(path,paint);
    }
}
