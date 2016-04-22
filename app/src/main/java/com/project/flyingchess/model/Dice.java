package com.project.flyingchess.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/4/13.
 */
public class Dice implements Serializable {
    private int random;
    private int color;
    private String message;

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
