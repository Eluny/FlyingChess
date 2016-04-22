package com.project.flyingchess.eventbus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class UpdateTitleEvent {
    private String msgContent;

    public UpdateTitleEvent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }
}
