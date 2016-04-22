package com.project.flyingchess.eventbus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class UpdateGameInfoEvent {
    private String msgContent;

    public UpdateGameInfoEvent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }
}
