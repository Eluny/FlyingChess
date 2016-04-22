package com.project.flyingchess.eventbus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class WinnerEvent {
    private String msgContent;

    public WinnerEvent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
}
