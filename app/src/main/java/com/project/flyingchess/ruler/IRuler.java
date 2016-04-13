package com.project.flyingchess.ruler;

/**
 * Created by Administrator on 2016/4/11.
 */
public interface IRuler {
    void start();

    void isWin();//谁是胜利的一方呀~
    //void isLose();

    void isEnd();//游戏是否终止~

    //Player nextPalyer();//游戏顺序~

    //void

    void dice();//扔个骰子

    void handle(int planeTag,int theSelectedPlaneTag);
}
