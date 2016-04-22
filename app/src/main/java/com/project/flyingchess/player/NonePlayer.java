package com.project.flyingchess.player;

/**
 * Created by Administrator on 2016/4/11.
 */
public class NonePlayer extends Player{
    public NonePlayer(String name,int color) {
        super(name,color);
        setFinish(false);
    }

    @Override
    public void think(int random) {
        getRuler().handle(0,0);
    }

    @Override
    public void onYourTurn() {
        getRuler().dice();
    }
}
