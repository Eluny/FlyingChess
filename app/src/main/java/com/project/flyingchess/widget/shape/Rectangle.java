package com.project.flyingchess.widget.shape;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.project.flyingchess.widget.ChessBoard;


/**
 * Created by wzm on 2016/3/26.
 */
public class Rectangle extends MyShape {
    public float left, top, right, bottom;
    public Paint paint;
    //public int num;

    public Rectangle() {
        super();
    }

    @Override
    public boolean isPointInRegion(float x,float y) {
        //Logger.d("x:" + x + " ,y:" + y + "\nleft" + left + " ,right" + right + " ,top" + top + " ,bottom" + bottom);
        if(x >= left && x <= right && y >= top && y <= bottom)
            return true;
        return false;
    }

    public void setCoord(float left, float top, float right, float bottom, int num,Paint paint) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.num = num;
        this.paint = paint;
        ChessBoard.planePosition.put(num, this);       //把矩形编号
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
