package com.project.flyingchess.widget.shape;

/**
 * Created by wzm on 2016/3/31.
 */
// ???
public abstract class MyShape {
    int num;

    public MyShape() {}

    public int getNum(){
        return num;
    };

    public abstract boolean isPointInRegion(float x,float y);
}
