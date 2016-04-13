package com.project.flyingchess.eventbus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class UpdateDiceEvent {
    private int number;

    public UpdateDiceEvent(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
