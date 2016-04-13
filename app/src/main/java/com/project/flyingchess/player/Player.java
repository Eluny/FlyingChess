package com.project.flyingchess.player;

import com.project.flyingchess.model.Step;
import com.project.flyingchess.ruler.IRuler;

/**
 * Created by Administrator on 2016/4/13.
 */
public class Player {
    private String name;
    private int color;
    private IRuler ruler;
    private int finishNum;

    public Player(String name,int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public IRuler getRuler() {
        return ruler;
    }

    public void setRuler(IRuler ruler) {
        this.ruler = ruler;
    }

    public int getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(int finishNum) {
        this.finishNum = finishNum;
    }

    public void think(int random){}

    public void putChess(Step step){}
}
