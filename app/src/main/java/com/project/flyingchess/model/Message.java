package com.project.flyingchess.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Administrator on 2016/4/18.
 */
@JsonObject
public class Message {
    public static final int MSG_BSAE = 0;
    public static final int MSG_TYPE_HOST_BEGIN = MSG_BSAE + 0;//房主开始游戏
    public static final int MSG_TYPE_BEGIN_ACK = MSG_BSAE + 1;//玩家收到房主开始游戏的确认消息
    public static final int MSG_TYPE_GAME_DATA = MSG_BSAE + 2;//游戏数据消息，包括下子位置
    public static final int MSG_TYPE_GAME_END = MSG_BSAE + 3;
    public static final int MSG_TYPE_GAME_RESTART_REQ = MSG_BSAE + 4;//重新开始游戏请求
    public static final int MSG_TYPE_GAME_RESTART_RESP = MSG_BSAE + 5;//重新开始游戏应答
    public static final int MSG_TYPE_EXIT = MSG_BSAE + 6;
    public static final int MSG_TYPE_MOVE_BACK_REQ = MSG_BSAE + 7;//悔棋请求
    public static final int MSG_TYPE_MOVE_BACK_RESP = MSG_BSAE + 8;//悔棋应答
    public static final int MSG_TYPE_DICE = MSG_BSAE + 9;//摇个骰子
    public static final int MSG_TYPE_YOUR_TURN = MSG_BSAE + 10;//摇个骰子
    public static final int MSG_TYPE_NOT_YOUR_TURN = MSG_BSAE + 11;//选择需要移动的飞机
    public static final int MSG_TYPE_SELECT_PLANE = MSG_BSAE + 12;//选择需要移动的飞机
    public static final int MSG_TYPE_NEXT_STEP = MSG_BSAE + 13;//选择需要移动的飞机

    @JsonField
    public int mMessageType;

    @JsonField
    public int mChessColor;

    @JsonField
    public int mRandom;

    @JsonField
    public int mPlaneTag;

    @JsonField
    public int mTheSelectedPlaneTag;

    @JsonField
    public Step mGameData;

    @JsonField
    public String mMessage;

    @JsonField
    public boolean mAgreeRestart;

    @JsonField
    public boolean mAgreeMoveBack;

    //使用@JsonObject必须提供public默认构造函数
    public Message() {
    }

    private Message(Builder builder) {
        this.mMessageType = builder.mMessageType;
        this.mChessColor = builder.mChessColor;
        this.mGameData = builder.mGameData;
        this.mMessage = builder.mMessage;
        this.mAgreeRestart = builder.mAgreeRestart;
        this.mAgreeMoveBack = builder.mAgreeMoveBack;
        this.mRandom = builder.mRandom;
        this.mPlaneTag = builder.mPlaneTag;
        this.mTheSelectedPlaneTag = builder.mTheSelectedPlaneTag;

    }

    //组装消息时建议使用Builder//但是为什么呢~？by:hunnny
    public static class Builder {
        private final int mMessageType;
        private int mChessColor;
        private int mRandom;
        private int mPlaneTag;
        private int mTheSelectedPlaneTag;
        private Step mGameData;
        private String mMessage;
        private boolean mAgreeRestart;
        private boolean mAgreeMoveBack;

        public Builder(int messageType) {
            mMessageType = messageType;
        }

        public Builder chessColor(int chessColor) {
            mChessColor = chessColor;
            return this;
        }

        public Builder random(int random) {
            mRandom = random;
            return this;
        }

        public Builder planeTag(int planeTag) {
            mPlaneTag = planeTag;
            return this;
        }

        public Builder theSelectedPlaneTag(int theSelectedPlaneTag) {
            mTheSelectedPlaneTag = theSelectedPlaneTag;
            return this;
        }

        public Builder gameData(Step gameData) {
            mGameData = gameData;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder agreeRestart(boolean agreeRestart) {
            mAgreeRestart = agreeRestart;
            return this;
        }

        public Builder agreeMoveBack(boolean agreeMoveBack) {
            mAgreeMoveBack = agreeMoveBack;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
