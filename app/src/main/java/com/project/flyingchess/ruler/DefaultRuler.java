package com.project.flyingchess.ruler;

import android.content.Context;

import com.bluelinelabs.logansquare.LoganSquare;
import com.orhanobut.logger.Logger;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.other.Constants;
import com.project.flyingchess.player.Player;
import com.project.flyingchess.utils.ACache;
import com.project.flyingchess.utils.Color;
import com.project.flyingchess.widget.ChessBoard;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/4/11.
 */
public class DefaultRuler implements IRuler{
    private Context mContext;

    public static int random;    //把private改成了public

    private Player currentPlayer;
    private List<Player> mList;
    private List<Player> mWinnerList = new ArrayList<>();
    private List<Step> stepList = new ArrayList<>();//记录棋局的行走情况

    private static final int FINISH_NUM = 1;
    private HashMap<Player,Integer> mFinishMap = new HashMap<>();

    private boolean isDicing = false;
    private boolean isStart = false;

    public DefaultRuler(Context mContext,List<Player> mList) {
        this.mContext = mContext;
        this.mList = mList;

        init();
    }


    public void init() {
        try{
            String str = ACache.get(mContext).getAsString("FlyingChess");
            if(!str.equals(" ")){
                List<Step> stepList_ = LoganSquare.parseList(str,Step.class);
                for(Step step:stepList_)
                    mList.get(0).putChess(step);
                //我还是服我自己的~
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mList.addAll(mWinnerList);
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
    public void uninit(){
        try {
            ACache.get(mContext).put("FlyingChess", LoganSquare.serialize(stepList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.d(ACache.get(mContext).getAsString("FlyingChess"));
    }

    @Override
    public void start() {
        Logger.d("The Game is Start~");
        EventBus.getDefault().post(new UpdateTitleEvent("游戏开始啦~"));

        nextPalyer();
        currentPlayer.onYourTurn();
        isDicing = true;
        isStart = true;
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
        if (mList.size() <= 1) {
            Logger.d(mWinnerList.toString());
            EventBus.getDefault().post(mWinnerList);
            isStart = false;
            return;
        }
        currentPlayer = (currentIndex != mList.size() ? mList.get(currentIndex) : mList.get(0));
    }

    private final Random randomGen = new Random();

    @Override
    public void dice() {
        if(isStart) {
            if (isDicing) {
                random = randomGen.nextInt(6) + 1;
                isDicing = false;
                if (isAllCanNotFly(currentPlayer.getColor())) {//所有的都不能飞喽~
                    EventBus.getDefault().post(new UpdateDiceEvent(random));
                    EventBus.getDefault().post(new UpdateTitleEvent(currentPlayer.getName() + "摇到的点数为：" + random));
                    nextPalyer();
                    isDicing = true;
                    currentPlayer.onYourTurn();
                } else {
                    currentPlayer.think(random);
                }
            }
        }
    }

    public boolean isAllCanNotFly(int color) {
        if (color == Color.BLUE) {
//            if (random != Constants.CAN_FLY && !ChessBoard.planes[0].isPre() && !ChessBoard.planes[1].isPre()
//                    && !ChessBoard.planes[2].isPre() && !ChessBoard.planes[3].isPre()) {
//                return true;
//            }
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(1) == ChessBoard.TAG_BLUE_BASE_1) && (ChessBoard.planeNum.get(2) == ChessBoard.TAG_BLUE_BASE_2)
                    && (ChessBoard.planeNum.get(3) == ChessBoard.TAG_BLUE_BASE_3) && (ChessBoard.planeNum.get(4) == ChessBoard.TAG_BLUE_BASE_4)) {
                return true;
            }
        }
        if (color == Color.YELLOW) {
//            if (random != Constants.CAN_FLY && !ChessBoard.planes[4].isPre() && !ChessBoard.planes[5].isPre()
//                    && !ChessBoard.planes[6].isPre() && !ChessBoard.planes[7].isPre()) {
//                return true;
//            }
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(5) == ChessBoard.TAG_YELLOW_BASE_1) && (ChessBoard.planeNum.get(6) == ChessBoard.TAG_YELLOW_BASE_2)
                    && (ChessBoard.planeNum.get(7) == ChessBoard.TAG_YELLOW_BASE_3) && (ChessBoard.planeNum.get(8) == ChessBoard.TAG_YELLOW_BASE_4)) {
                return true;
            }
        }
        if (color == Color.RED) {
//            if (random != Constants.CAN_FLY && !ChessBoard.planes[8].isPre() && !ChessBoard.planes[9].isPre()
//                    && !ChessBoard.planes[10].isPre() && !ChessBoard.planes[11].isPre()) {
//                return true;
//            }
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(9) == ChessBoard.TAG_RED_BASE_1) && (ChessBoard.planeNum.get(10) == ChessBoard.TAG_RED_BASE_2)
                    && (ChessBoard.planeNum.get(11) == ChessBoard.TAG_RED_BASE_3) && (ChessBoard.planeNum.get(12) == ChessBoard.TAG_RED_BASE_4)) {
                return true;
            }
        }
        if (color == Color.GREEN) {
//            if (random != Constants.CAN_FLY && !ChessBoard.planes[12].isPre() && !ChessBoard.planes[13].isPre()
//                    && !ChessBoard.planes[14].isPre() && !ChessBoard.planes[15].isPre()) {
//                return true;
//            }
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(13) == ChessBoard.TAG_GREEN_BASE_1) && (ChessBoard.planeNum.get(14) == ChessBoard.TAG_GREEN_BASE_2)
                    && (ChessBoard.planeNum.get(15) == ChessBoard.TAG_GREEN_BASE_3) && (ChessBoard.planeNum.get(16) == ChessBoard.TAG_GREEN_BASE_4)) {
                return true;
            }
        }
        return false;
    }

    /*
     *棋子若行进到颜色相同而有虚线连接的一格，可照虚线箭头指示的路线，通过虚线到前方颜色相同的的一格后，
     * 再跳至下一个与棋子颜色相同的格内；若棋子是由上一个颜色相同的格子跳至颜色相同而有虚线连接的一格内，
     * 则棋子照虚线箭头指示的路线，通过虚线到前方颜色相同的一格后，棋子就不再移动。
     */
    private Step step = null;
    @Override
    public void handle(int planeTag, int theSelectedPlaneTag) {
        Logger.d("planeTag : " + planeTag + " ,theSelectedPlaneTag : " + theSelectedPlaneTag);
        //TODO:这里写的是...逻辑上的控制~反馈给player~让它自己去控制棋盘~这样做是因为...想把联网的和本地的写一块~
       // Step step = null;

        switch (currentPlayer.getColor()){
            case Color.BLUE:
                //暂时添加
                if (random == Constants.CAN_FLY) {            //一不小心摇到了6
                    if (ChessBoard.TAG_BLUE_BASE_1 <= theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_BLUE_BASE_4) {   //还没起飞呢
                        step = new Step(planeTag, ChessBoard.TAG_BLUE_PRE);
                    } else {
                        handleBlue(planeTag, theSelectedPlaneTag);
                    }
                } else {
                    handleBlue(planeTag, theSelectedPlaneTag);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            case Color.YELLOW:
                //暂时添加
                if (random == Constants.CAN_FLY) {            //一不小心摇到了6
                    if (ChessBoard.TAG_YELLOW_BASE_1 <= theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_YELLOW_BASE_4) {   //还没起飞呢
                        step = new Step(planeTag, ChessBoard.TAG_YELLOW_PRE);
                    } else {
                        handleYellow(planeTag, theSelectedPlaneTag);
                    }
                } else {
                    handleYellow(planeTag, theSelectedPlaneTag);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            case Color.RED:
                //暂时添加
                if (random == Constants.CAN_FLY) {            //一不小心摇到了6
                    if (ChessBoard.TAG_RED_BASE_1 <= theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RED_BASE_4) {   //还没起飞呢
                        step = new Step(planeTag, ChessBoard.TAG_RED_PRE);
                    } else {
                        handleRed(planeTag, theSelectedPlaneTag);
                    }
                } else {
                    handleRed(planeTag, theSelectedPlaneTag);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            case Color.GREEN:
                //暂时添加
                if (random == Constants.CAN_FLY) {            //一不小心摇到了6
                    if (ChessBoard.TAG_GREEN_BASE_1 <= theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_GREEN_BASE_4) {   //还没起飞呢
                        step = new Step(planeTag, ChessBoard.TAG_GREEN_PRE);
                    } else {
                        handleGreen(planeTag, theSelectedPlaneTag);
                    }
                } else {
                    handleGreen(planeTag, theSelectedPlaneTag);
                }
                stepList.add(step);
                currentPlayer.putChess(step);
                break;
            default:
                break;
        }

        if (random == Constants.CAN_FLY) {       //摇到6就奖励一次
            isDicing = true;
            currentPlayer.onYourTurn();
        } else {
            nextPalyer();
            isDicing = true;
            currentPlayer.onYourTurn();
        }
    }

    public void handleBlue(int planeTag, int theSelectedPlaneTag) {
        if (theSelectedPlaneTag == ChessBoard.TAG_BLUE_PRE) {    //假如在起飞点
            if ((ChessBoard.TAG_BLUE_START + random - ChessBoard.TAG_BLUE_JUMP) % 4 == 0) {
                step = new Step(planeTag, ChessBoard.TAG_BLUE_START + random + 4);
            } else {
                step = new Step(planeTag, ChessBoard.TAG_BLUE_START + random);
            }
        } else {                                                      //不在起飞点
            if (0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_BLUE_CORNER) {   //在外环途中
                if (theSelectedPlaneTag + random < ChessBoard.TAG_BLUE_CORNER) {                   //加上点数都还在外环
                    if (theSelectedPlaneTag + random == ChessBoard.TAG_BLUE_DOUBLE_JUMP_START1) {     //第一种连跳
                        step = new Step(planeTag, ChessBoard.TAG_BLUE_DOUBLE_JUMP_END1);
                    } else if (theSelectedPlaneTag + random == ChessBoard.TAG_BLUE_DOUBLE_JUMP_START2) {   //第二种连跳
                        step = new Step(planeTag, ChessBoard.TAG_BLUE_DOUBLE_JUMP_END2);
                    } else if ((theSelectedPlaneTag + random - ChessBoard.TAG_BLUE_JUMP) % 4 == 0) {
                        step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random);
                    }
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_BLUE_CORNER) {
                    step = new Step(planeTag, ChessBoard.TAG_BLUE_CORNER);
                } else {
                    step = new Step(planeTag, ChessBoard.TAG_BLUE_CORNER_START + theSelectedPlaneTag + random - ChessBoard.TAG_BLUE_CORNER);
                }
            } else{
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
        }
    }

    public void handleYellow(int planeTag, int theSelectedPlaneTag) {    //感觉怪怪的，不知道有没有错呢。。。
        if (theSelectedPlaneTag == ChessBoard.TAG_YELLOW_PRE) {    //假如在起飞点
            if ((ChessBoard.TAG_YELLOW_START + random - ChessBoard.TAG_YELLOW_JUMP) % 4 == 0) {
                step = new Step(planeTag, ChessBoard.TAG_YELLOW_START + random + 4);
            } else {
                step = new Step(planeTag, ChessBoard.TAG_YELLOW_START + random);
            }
        } else {                                                      //不在起飞点
            if (ChessBoard.TAG_YELLOW_START < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RECTANGLE_LARGE) {
                if (theSelectedPlaneTag + random == ChessBoard.TAG_YELLOW_DOUBLE_JUMP_START1) {     //先解决特殊的几个点
                    step = new Step(planeTag, ChessBoard.TAG_YELLOW_DOUBLE_JUMP_END1);
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_YELLOW_DOUBLE_JUMP_START2) {
                    step = new Step(planeTag, ChessBoard.TAG_YELLOW_DOUBLE_JUMP_END2);
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_RECTANGLE_LARGE - 1) {
                    step = new Step(planeTag, 3);
                } else if (theSelectedPlaneTag + random > ChessBoard.TAG_RECTANGLE_LARGE) {
                    if ((theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE == 3) {
                        step = new Step(planeTag, 7);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random - ChessBoard.TAG_RECTANGLE_LARGE);
                    }
                } else if ((theSelectedPlaneTag + random - ChessBoard.TAG_YELLOW_JUMP) % 4 == 0) {
                    step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                } else {
                    step = new Step(planeTag, theSelectedPlaneTag + random);
                }
            } else if (0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_YELLOW_CORNER) {
                if (theSelectedPlaneTag + random < ChessBoard.TAG_YELLOW_CORNER) {
                    if ((theSelectedPlaneTag + random - ChessBoard.TAG_YELLOW_JUMP) % 4 == 0) {
                        step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random);
                    }
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_YELLOW_CORNER) {
                    step = new Step(planeTag, ChessBoard.TAG_YELLOW_CORNER);
                } else {
                    step = new Step(planeTag, theSelectedPlaneTag + random - ChessBoard.TAG_YELLOW_CORNER + ChessBoard.TAG_YELLOW_CORNER_START);
                }
            } else {
                if (theSelectedPlaneTag + random <= ChessBoard.TAG_YELLOW_END) {
                    step = new Step(planeTag,theSelectedPlaneTag + random);
                    if (theSelectedPlaneTag + random == ChessBoard.TAG_YELLOW_END) {
                        mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                        if (mFinishMap.get(currentPlayer) == FINISH_NUM) {
                            currentPlayer.setFinish(true);
                            mWinnerList.add(currentPlayer);
                            if (mList.size() <= 1) {
                                Logger.d(mWinnerList.toString());
                                EventBus.getDefault().post(mWinnerList);
                                return;
                            }
                        }
                    }
                } else {
                    step = new Step(planeTag, ChessBoard.TAG_YELLOW_END * 2 - (theSelectedPlaneTag + random));
                }
            }
        }
    }

    public void handleRed(int planeTag, int theSelectedPlaneTag) {    //感觉怪怪的，不知道有没有错呢。。。
        if (theSelectedPlaneTag == ChessBoard.TAG_RED_PRE) {    //假如在起飞点
            if ((ChessBoard.TAG_RED_START + random - ChessBoard.TAG_RED_JUMP) % 4 == 0) {
                step = new Step(planeTag, ChessBoard.TAG_RED_START + random + 4);
            } else {
                step = new Step(planeTag, ChessBoard.TAG_RED_START + random);
            }
        } else {                                                      //不在起飞点
            if (ChessBoard.TAG_RED_START < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RECTANGLE_LARGE) {
                if (theSelectedPlaneTag + random == ChessBoard.TAG_RED_DOUBLE_JUMP_START1) {     //先解决特殊的几个点
                    step = new Step(planeTag, ChessBoard.TAG_RED_DOUBLE_JUMP_END1);
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_RED_DOUBLE_JUMP_START2) {
                    step = new Step(planeTag, ChessBoard.TAG_RED_DOUBLE_JUMP_END2);
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_RECTANGLE_LARGE) {
                    step = new Step(planeTag, 4);
                } else if (theSelectedPlaneTag + random > ChessBoard.TAG_RECTANGLE_LARGE) {
                    if ((theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE == 4) {
                        step = new Step(planeTag, 8);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random - ChessBoard.TAG_RECTANGLE_LARGE);
                    }
                } else if ((theSelectedPlaneTag + random - ChessBoard.TAG_RED_JUMP) % 4 == 0) {
                    step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                } else {
                    step = new Step(planeTag, theSelectedPlaneTag + random);
                }
            } else if (0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RED_CORNER) {
                if (theSelectedPlaneTag + random < ChessBoard.TAG_RED_CORNER) {
                    if ((theSelectedPlaneTag + random - ChessBoard.TAG_RED_JUMP) % 4 == 0) {
                        step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random);
                    }
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_RED_CORNER) {
                    step = new Step(planeTag, ChessBoard.TAG_RED_CORNER);
                } else {
                    step = new Step(planeTag, theSelectedPlaneTag + random - ChessBoard.TAG_RED_CORNER + ChessBoard.TAG_RED_CORNER_START);
                }
            } else {
                if (theSelectedPlaneTag + random <= ChessBoard.TAG_RED_END) {
                    step = new Step(planeTag,theSelectedPlaneTag + random);
                    if (theSelectedPlaneTag + random == ChessBoard.TAG_RED_END) {
                        mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                        if (mFinishMap.get(currentPlayer) == FINISH_NUM) {
                            currentPlayer.setFinish(true);
                            mWinnerList.add(currentPlayer);
                            if (mList.size() <= 1) {
                                Logger.d(mWinnerList.toString());
                                EventBus.getDefault().post(mWinnerList);
                                return;
                            }
                        }
                    }
                } else {
                    step = new Step(planeTag, ChessBoard.TAG_RED_END * 2 - (theSelectedPlaneTag + random));
                }
            }
        }
    }

    public void handleGreen(int planeTag, int theSelectedPlaneTag) {    //感觉怪怪的，不知道有没有错呢。。。
        if (theSelectedPlaneTag == ChessBoard.TAG_GREEN_PRE) {    //假如在起飞点
            if ((ChessBoard.TAG_GREEN_START + random - ChessBoard.TAG_GREEN_JUMP) % 4 == 0) {
                step = new Step(planeTag, ChessBoard.TAG_GREEN_START + random + 4);
            } else {
                step = new Step(planeTag, ChessBoard.TAG_GREEN_START + random);
            }
        } else {                                                      //不在起飞点
            if (ChessBoard.TAG_GREEN_START < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_RECTANGLE_LARGE) {
                if (theSelectedPlaneTag + random == ChessBoard.TAG_RECTANGLE_LARGE - 3) {
                    step = new Step(planeTag, 1);
                } else if (theSelectedPlaneTag + random > ChessBoard.TAG_RECTANGLE_LARGE) {
                    if ((theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE == ChessBoard.TAG_GREEN_DOUBLE_JUMP_START1) {
                        step = new Step(planeTag, ChessBoard.TAG_GREEN_DOUBLE_JUMP_END1);
                    } else if ((theSelectedPlaneTag + random) % ChessBoard.TAG_RECTANGLE_LARGE == ChessBoard.TAG_GREEN_DOUBLE_JUMP_START2) {
                        step = new Step(planeTag, ChessBoard.TAG_GREEN_DOUBLE_JUMP_END2);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random - ChessBoard.TAG_RECTANGLE_LARGE);
                    }
                } else if ((theSelectedPlaneTag + random - ChessBoard.TAG_GREEN_JUMP) % 4 == 0) {
                    step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                } else {
                    step = new Step(planeTag, theSelectedPlaneTag + random);
                }
            } else if (0 < theSelectedPlaneTag && theSelectedPlaneTag <= ChessBoard.TAG_GREEN_CORNER) {
                if (theSelectedPlaneTag + random == ChessBoard.TAG_GREEN_DOUBLE_JUMP_START2) {
                    step = new Step(planeTag, ChessBoard.TAG_GREEN_DOUBLE_JUMP_END2);
                } else if (theSelectedPlaneTag + random < ChessBoard.TAG_GREEN_CORNER) {
                    if ((theSelectedPlaneTag + random - ChessBoard.TAG_GREEN_JUMP) % 4 == 0) {
                        step = new Step(planeTag, theSelectedPlaneTag + random + 4);
                    } else {
                        step = new Step(planeTag, theSelectedPlaneTag + random);
                    }
                } else if (theSelectedPlaneTag + random == ChessBoard.TAG_GREEN_CORNER) {
                    step = new Step(planeTag, ChessBoard.TAG_GREEN_CORNER);
                } else {
                    step = new Step(planeTag, theSelectedPlaneTag + random - ChessBoard.TAG_GREEN_CORNER + ChessBoard.TAG_GREEN_CORNER_START);
                }
            } else {
                if (theSelectedPlaneTag + random <= ChessBoard.TAG_GREEN_END) {
                    step = new Step(planeTag,theSelectedPlaneTag + random);
                    if (theSelectedPlaneTag + random == ChessBoard.TAG_GREEN_END) {
                        mFinishMap.put(currentPlayer,mFinishMap.get(currentPlayer)+1);
                        if (mFinishMap.get(currentPlayer) == FINISH_NUM) {
                            currentPlayer.setFinish(true);
                            mWinnerList.add(currentPlayer);
                            if (mList.size() <= 1) {
                                Logger.d(mWinnerList.toString());
                                EventBus.getDefault().post(mWinnerList);
                                return;
                            }
                        }
                    }
                } else {
                    step = new Step(planeTag, ChessBoard.TAG_GREEN_END * 2 - (theSelectedPlaneTag + random));
                }
            }
        }
    }
}