package com.project.flyingchess.ruler;

/**
 * Created by Administrator on 2016/4/11.
 */
public interface IRuler {
    int PORT = 23333;

    void start();

    void restart();

    void init();

    void uninit();

    void dice();//扔个骰子

    void handle(int planeTag,int theSelectedPlaneTag);
}
