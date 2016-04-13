package com.project.flyingchess.ruler;

import com.orhanobut.logger.Logger;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.WinnerEvent;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.player.Color;
import com.project.flyingchess.player.Player;
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
    private boolean isStart = false;

    private List<Player> mList;
    private HashMap<Player,Integer> mFinishMap = new HashMap<>();
    private Player currentPlayer;

    private static int random;
    private List<Step> stepList = new ArrayList<>();//记录棋局的行走情况

    private static final int FINISH_NUM = 4;

    public DefaultRuler(List<Player> mList) {
        this.mList = mList;
    }

    @Override
    public void start() {
        Logger.d("The Game is Start~");
        for(Player player:mList){
            player.setRuler(this);//循环引用~我就不造会不会死了
            mFinishMap.put(player,0);
        }

        isStart = true;
    }

    @Override
    public void isWin() {

    }

    @Override
    public void isEnd() {
        Logger.d("The Game is Over~");
    }

    public void nextPalyer() {
        if(currentPlayer == null){
            currentPlayer = mList.get(0);
            return;
        }
        int currentIndex = mList.indexOf(currentPlayer) + 1;
        Logger.d(currentIndex + ": current");
        currentPlayer = (currentIndex != mList.size() ? mList.get(currentIndex) : mList.get(0));
    }

    private final Random randomGen = new Random();

    @Override
    public void dice() {
        if(isStart){
            random = randomGen.nextInt(6) + 1;
            EventBus.getDefault().post(new UpdateDiceEvent(random));
            nextPalyer();
            Logger.d("currentPlayer ~ :" + currentPlayer.getColor() + " dicing : " + random);
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
                                EventBus.getDefault().post(new WinnerEvent(currentPlayer.getName()));
                            }
                        }
                    }
                    else
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_END * 2 - (theSelectedPlaneTag + random));
                }
                /*else if(theSelectedPlaneTag + random <= ChessBoard.TAG_BLUE_CORNER){
                    step = new Step(planeTag,theSelectedPlaneTag + random);
                }else{
                    if(theSelectedPlaneTag < ChessBoard.TAG_BLUE_CORNER){
                        step = new Step(planeTag,ChessBoard.TAG_BLUE_CORNER_START + theSelectedPlaneTag + random - ChessBoard.TAG_BLUE_CORNER);
                    }else{
                        if(theSelectedPlaneTag + random <= ChessBoard.TAG_BLUE_END)
                            step = new Step(planeTag,theSelectedPlaneTag + random);
                        else
                            step = new Step(planeTag,ChessBoard.TAG_BLUE_END * 2 - (theSelectedPlaneTag + random));
                    }
                }*/
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
                                EventBus.getDefault().post(new WinnerEvent(currentPlayer.getName()));
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
                                EventBus.getDefault().post(new WinnerEvent(currentPlayer.getName()));
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
                                EventBus.getDefault().post(new WinnerEvent(currentPlayer.getName()));
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
                break;
        }

        random = 0;
        //EventBus.getDefault().post(new UpdateDiceEvent(0));
        Logger.d("step:" + step);
        stepList.add(step);
        currentPlayer.putChess(step);
    }
}