package com.project.flyingchess.player;

import com.project.flyingchess.model.Step;
import com.project.flyingchess.ruler.IRuler;

import java.util.List;

/**
 * Created by Administrator on 2016/4/13.
 */
public class Player {
    private String name;
    private int color;
    private IRuler ruler;
    private boolean isFinish;

    public Player() {
    }

    public Player(String name, int color) {
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

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public void think(int random){}

    public void putChess(Step step){}

    public void onYourTurn(){}

    public void onYourTurn(boolean isYourTurn,String content){}

    public void start(int color){}

    public void end(List<Player> mWinnerList){}

    public void restart(){}

    public void exit(){}//因为各种莫名其妙的细节~
}

