package com.project.flyingchess.ruler;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.player.Player;
import com.project.flyingchess.utils.Color;
import com.project.flyingchess.widget.ChessBoard;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/4/11.
 */
public class DefaultRuler implements IRuler{
    private Context mContext;

    private static int random;

    private Player currentPlayer;
    private List<Player> mList;
    private List<Player> mWinnerList = new ArrayList<>();
    private List<Step> stepList = new ArrayList<>();//记录棋局的行走情况

    private static final int FINISH_NUM = 1;
    private HashMap<Player,Integer> mFinishMap = new HashMap<>();

    private boolean isDicing = false;

    public DefaultRuler(List<Player> mList) {
        this.mList = mList;

        init();
    }


    public void init() {
        mWinnerList.clear();
        stepList.clear();
        mFinishMap.clear();

        for(Player player:mList){
            player.setRuler(this);//循环引用~我就不造会不会死了
            player.setFinish(false);
            mFinishMap.put(player,0);
        }
    }

    @Override
    public void uninit() {
    }

    @Override
    public void start() {
        Logger.d("The Game is Start~");
        EventBus.getDefault().post(new UpdateTitleEvent("游戏开始啦~"));

        nextPalyer();
        currentPlayer.onYourTurn();
        isDicing = true;
    }

    @Override
    public void restart() {
        init();
        start();
    }

    public void nextPalyer() {
        if(currentPlayer == null){
            currentPlayer = mList.get(0);
            return;
        }
        int currentIndex = mList.indexOf(currentPlayer) + 1;
        if(mWinnerList.size() != 0)
            mList.remove(mWinnerList.get(mWinnerList.size()-1));
        Logger.d(currentIndex + ": current");
        currentPlayer = (currentIndex != mList.size() ? mList.get(currentIndex) : mList.get(0));
    }

    private final Random randomGen = new Random();

    @Override
    public void dice() {
        if(isDicing){
            random = randomGen.nextInt(6) + 1;
            isDicing = false;
            currentPlayer.think(random);
        }
    }

    @Override
    public void handle(int planeTag, int theSelectedPlaneTag) {
        Logger.d("planeTag : " + planeTag + " ,theSelectedPlaneTag : " + theSelectedPlaneTag);
        //TODO:这里写的是...逻辑上的控制~反馈给player~让它自己去控制棋盘~这样做是因为...想把联网的和本地的写一块~
        Step step = null;
        switch (currentPlayer.getColor()){
            case Color.BLUE:
                if(ChessBoard.TAG_LARGE <= theSelectedPlaneTag || ChessBoard.TAG_SMALL >= theSelectedPlaneTag)
                    if((ChessBoard.TAG_BLUE_START + random - ChessBoard.TAG_BLUE_JUMP) % 4 == 0)
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_START + random + 4);
                    else
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_START + random);
                else if(0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_BLUE_CORNER){
                    if(theSelectedPlaneTag + random < ChessBoard.TAG_BLUE_CORNER)
                        if((theSelectedPlaneTag + random - ChessBoard.TAG_BLUE_JUMP) % 4 == 0){
                            step = new Step(planeTag,theSelectedPlaneTag + random + 4);
                        } else {
                            step = new Step(planeTag,theSelectedPlaneTag + random);
                        }
                    else if(theSelectedPlaneTag + random == ChessBoard.TAG_BLUE_CORNER){
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_CORNER);
                    }else{
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_CORNER_START + theSelectedPlaneTag + random - ChessBoard.TAG_BLUE_CORNER);
                    }
                }else{
                    if(theSelectedPlaneTag + random <= ChessBoard.TAG_BLUE_END){
                        step = new Step(planeTag,theSelectedPlaneTag + random);
                        if(theSelectedPlaneTag + random == ChessBoard.TAG_BLUE_END){
                            mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                            if(mFinishMap.get(currentPlayer) == FINISH_NUM){
                                currentPlayer.setFinish(true);
                                mWinnerList.add(currentPlayer);
                                if(mList.size() <= 1) {
                                    Logger.d(mWinnerList.toString());
                                    EventBus.getDefault().post(mWinnerList);
                                    return;
                                }
                            }
                        }
                    }
                    else
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_END * 2 - (theSelectedPlaneTag + random));
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            case Color.YELLOW:
                if(ChessBoard.TAG_LARGE <= theSelectedPlaneTag || ChessBoard.TAG_SMALL >= theSelectedPlaneTag)
                    if((ChessBoard.TAG_YELLOW_START + random - ChessBoard.TAG_YELLOW_JUMP) % 4 == 0)
                        step = new Step(planeTag,ChessBoard.TAG_YELLOW_START + random + 4);
                    else
                        step = new Step(planeTag,ChessBoard.TAG_YELLOW_START + random);
                else if(0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_YELLOW_CORNER) {
                    if(theSelectedPlaneTag + random < ChessBoard.TAG_YELLOW_CORNER)
                        if((ChessBoard.TAG_YELLOW_JUMP - theSelectedPlaneTag - random) % 4 == 0)
                            step = new Step(planeTag,theSelectedPlaneTag + random + 4);
                        else
                            step = new Step(planeTag,theSelectedPlaneTag + random);
                    else if(theSelectedPlaneTag + random == ChessBoard.TAG_YELLOW_CORNER){
                        step = new Step(planeTag,ChessBoard.TAG_YELLOW_CORNER);
                    }else{
                        step = new Step(planeTag,ChessBoard.TAG_YELLOW_CORNER_START + theSelectedPlaneTag + random - ChessBoard.TAG_YELLOW_CORNER);
                    }
                }else if(ChessBoard.TAG_YELLOW_CORNER_START < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_YELLOW_END){
                    if(theSelectedPlaneTag + random <= ChessBoard.TAG_YELLOW_END){
                        step = new Step(planeTag,theSelectedPlaneTag + random);
                        if(theSelectedPlaneTag + random == ChessBoard.TAG_YELLOW_END){
                            mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                            if(mFinishMap.get(currentPlayer) == FINISH_NUM){
                                currentPlayer.setFinish(true);
                                mWinnerList.add(currentPlayer);
                                if(mList.size() <= 1) {
                                    Logger.d(mWinnerList.toString());
                                    EventBus.getDefault().post(mWinnerList);
                                    return;
                                }
                                //EventBus.getDefault().post(new WinnerEvent(currentPlayer.getName()));
                            }
                        }
                    }
                    else
                        step = new Step(planeTag,ChessBoard.TAG_YELLOW_END * 2 - (theSelectedPlaneTag + random));
                }else /* if(theSelectedPlaneTag <= ChessBoard.TAG_RECTANGLE_LARGE)*/{
                    if((theSelectedPlaneTag + random - ChessBoard.TAG_YELLOW_JUMP) % 4 == 0)
                        step = new Step(planeTag,(theSelectedPlaneTag + random + 4) % ChessBoard.TAG_RECTANGLE_LARGE);
                    else
                        step = new Step(planeTag,(theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            case Color.RED:
                if(ChessBoard.TAG_LARGE <= theSelectedPlaneTag || ChessBoard.TAG_SMALL >= theSelectedPlaneTag)
                    if((ChessBoard.TAG_RED_START + random - ChessBoard.TAG_RED_JUMP) % 4 == 0)
                        step = new Step(planeTag,ChessBoard.TAG_RED_START + random + 4);
                    else
                        step = new Step(planeTag,ChessBoard.TAG_RED_START + random);
                else if(0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RED_CORNER) {
                    if(theSelectedPlaneTag + random < ChessBoard.TAG_RED_CORNER)
                        if((ChessBoard.TAG_RED_JUMP - theSelectedPlaneTag - random) % 4 == 0)
                            step = new Step(planeTag,theSelectedPlaneTag + random + 4);
                        else
                            step = new Step(planeTag,theSelectedPlaneTag + random);
                    else if(theSelectedPlaneTag + random == ChessBoard.TAG_RED_CORNER){
                        step = new Step(planeTag,ChessBoard.TAG_RED_CORNER);
                    }else{
                        step = new Step(planeTag,ChessBoard.TAG_RED_CORNER_START + theSelectedPlaneTag + random - ChessBoard.TAG_RED_CORNER);
                    }
                }else if(ChessBoard.TAG_RED_CORNER_START < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RED_END){
                    if(theSelectedPlaneTag + random <= ChessBoard.TAG_RED_END){
                        step = new Step(planeTag,theSelectedPlaneTag + random);
                        if(theSelectedPlaneTag + random == ChessBoard.TAG_RED_END){
                            mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                            if(mFinishMap.get(currentPlayer) == FINISH_NUM){
                                currentPlayer.setFinish(true);
                                mWinnerList.add(currentPlayer);
                                if(mList.size() <= 1) {
                                    Logger.d(mWinnerList.toString());
                                    EventBus.getDefault().post(mWinnerList);
                                    return;
                                }
                            }
                        }
                    }
                    else
                        step = new Step(planeTag,ChessBoard.TAG_RED_END * 2 - (theSelectedPlaneTag + random));
                }else /* if(theSelectedPlaneTag <= ChessBoard.TAG_RECTANGLE_LARGE)*/{
                    if((theSelectedPlaneTag + random - ChessBoard.TAG_RED_JUMP) % 4 == 0)
                        step = new Step(planeTag,(theSelectedPlaneTag + random + 4) % ChessBoard.TAG_RECTANGLE_LARGE);
                    else
                        step = new Step(planeTag,(theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            case Color.GREEN:
                if(ChessBoard.TAG_LARGE <= theSelectedPlaneTag || ChessBoard.TAG_SMALL >= theSelectedPlaneTag)
                    if((ChessBoard.TAG_GREEN_START + random - ChessBoard.TAG_GREEN_JUMP) % 4 == 0)
                        step = new Step(planeTag,ChessBoard.TAG_GREEN_START + random + 4);
                    else
                        step = new Step(planeTag,ChessBoard.TAG_GREEN_START + random);
                else if(0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_GREEN_CORNER) {
                    if(theSelectedPlaneTag + random < ChessBoard.TAG_GREEN_CORNER)
                        if((ChessBoard.TAG_GREEN_JUMP - theSelectedPlaneTag - random) % 4 == 0)
                            step = new Step(planeTag,theSelectedPlaneTag + random + 4);
                        else
                            step = new Step(planeTag,theSelectedPlaneTag + random);
                    else if(theSelectedPlaneTag + random == ChessBoard.TAG_GREEN_CORNER){
                        step = new Step(planeTag,ChessBoard.TAG_GREEN_CORNER);
                    }else{
                        step = new Step(planeTag,ChessBoard.TAG_GREEN_CORNER_START + theSelectedPlaneTag + random - ChessBoard.TAG_GREEN_CORNER);
                    }
                }else if(ChessBoard.TAG_GREEN_CORNER_START < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_GREEN_END){
                    if(theSelectedPlaneTag + random <= ChessBoard.TAG_GREEN_END){
                        step = new Step(planeTag,theSelectedPlaneTag + random);
                        if(theSelectedPlaneTag + random == ChessBoard.TAG_GREEN_END){
                            mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                            if(mFinishMap.get(currentPlayer) == FINISH_NUM){
                                currentPlayer.setFinish(true);
                                mWinnerList.add(currentPlayer);
                                if(mList.size() <= 1) {
                                    Logger.d(mWinnerList.toString());
                                    EventBus.getDefault().post(mWinnerList);
                                }
                            }
                        }
                    }
                    else
                        step = new Step(planeTag,ChessBoard.TAG_GREEN_END * 2 - (theSelectedPlaneTag + random));
                }else /* if(theSelectedPlaneTag <= ChessBoard.TAG_RECTANGLE_LARGE)*/{
                    if((theSelectedPlaneTag + random - ChessBoard.TAG_GREEN_JUMP) % 4 == 0)
                        step = new Step(planeTag,(theSelectedPlaneTag + random + 4) % ChessBoard.TAG_RECTANGLE_LARGE);
                    else
                        step = new Step(planeTag,(theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            default:
                break;
        }

        nextPalyer();
        isDicing = true;
        currentPlayer.onYourTurn();
    }
}