package com.project.flyingchess.utils;

import com.project.flyingchess.model.Message;
import com.project.flyingchess.model.Step;

/**
 * Created by lenov0 on 2016/1/25.
 */
public class MessageWrapper {

    public static Message getHostBeginMessage(int color) {
        return new Message.Builder(Message.MSG_TYPE_HOST_BEGIN).chessColor(color).build();
    }

    public static Message getHostBeginAckMessage() {
        return new Message.Builder(Message.MSG_TYPE_BEGIN_ACK).build();
    }

    public static Message getSendDataMessage(Step step) {
        return new Message.Builder(Message.MSG_TYPE_GAME_DATA).gameData(step).build();
    }

    public static Message getDiceMessage(int random, String message) {
        return new Message.Builder(Message.MSG_TYPE_DICE).random(random).message(message).build();
    }

    public static Message getDiceMessage(int random, int chessColor) {
        return new Message.Builder(Message.MSG_TYPE_DICE).random(random).chessColor(chessColor).build();
    }

    public static Message getSelectPlaneMessage(int planeTag, int theSelectedPlaneTag) {
        return new Message.Builder(Message.MSG_TYPE_SELECT_PLANE).planeTag(planeTag).theSelectedPlaneTag(theSelectedPlaneTag).build();
    }

    public static Message getYourTurnMessage() {
        return new Message.Builder(Message.MSG_TYPE_YOUR_TURN).build();
    }
    public static Message getNotYourTurnMessage(String message) {
        return new Message.Builder(Message.MSG_TYPE_NOT_YOUR_TURN).message(message).build();
    }


    public static Message getGameEndMessage(String endMessage) {
        return new Message.Builder(Message.MSG_TYPE_GAME_END).message(endMessage).build();
    }

    public static Message getGameRestartReqMessage() {
        return new Message.Builder(Message.MSG_TYPE_GAME_RESTART_REQ).build();
    }

    public static Message getGameRestartRespMessage(boolean agreeRestart) {
        return new Message.Builder(Message.MSG_TYPE_GAME_RESTART_RESP).agreeRestart(agreeRestart).build();
    }

    public static Message getGameExitMessage(String name) {
        return new Message.Builder(Message.MSG_TYPE_EXIT).message(name).build();
    }

    public static Message getGameMoveBackReqMessage() {
        return new Message.Builder(Message.MSG_TYPE_MOVE_BACK_REQ).build();
    }

    public static Message getGameMoveBackRespMessage(boolean agreeMoveBack) {
        return new Message.Builder(Message.MSG_TYPE_MOVE_BACK_RESP).agreeMoveBack(agreeMoveBack).build();
    }
}