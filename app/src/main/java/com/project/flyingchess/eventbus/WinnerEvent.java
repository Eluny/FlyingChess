package com.project.flyingchess.eventbus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class WinnerEvent {
    private String name;

    public WinnerEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
