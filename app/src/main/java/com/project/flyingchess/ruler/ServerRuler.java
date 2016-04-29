package com.project.flyingchess.ruler;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.orhanobut.logger.Logger;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.project.flyingchess.R;
import com.project.flyingchess.activity.ConfigueActivity;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateGameInfoEvent;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.model.Message;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.other.Constants;
import com.project.flyingchess.player.ClientPlayer;
import com.project.flyingchess.player.Player;
import com.project.flyingchess.utils.Color;
import com.project.flyingchess.widget.ChessBoard;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by hunnny on 2016/4/11.
 */
public class ServerRuler implements IRuler,SalutDataCallback {
    private Context mContext;

    private Player localPlayer;
    private Player currentPlayer;
    private List<Player> mList = new ArrayList<>();;
    private List<Player> mWinnerList = new ArrayList<>();
    private List<Player> mEyeList = new ArrayList<>();
    private List<Integer> mColorList = new ArrayList<>();//用来随机生成颜色的~？

    private static final int FINISH_NUM = 4;
    private HashMap<Player,Integer> mFinishMap = new HashMap<>();

    private static int random;
    private List<Step> stepList = new ArrayList<>();//记录棋局的行走情况

    private Salut mSalut;

    private boolean isStart = false;
    private boolean isEnd = false;
    private boolean isYourTurn = false;//骰子~

    public ServerRuler(Context mContext, Player currentPlayer) {
        this.mContext = mContext;
        this.currentPlayer = currentPlayer;

        init();
    }

    @Override
    public void init() {
        localPlayer = currentPlayer;

        mList.add(currentPlayer);

        currentPlayer.setRuler(this);

        //mColorList.add(Color.BLUE);
        mColorList.add(Color.YELLOW);
        mColorList.add(Color.RED);
        mColorList.add(Color.GREEN);

        if (!Salut.isWiFiEnabled(mContext)) {
            Salut.enableWiFi(mContext);
        }

        SalutDataReceiver mDataReceiver = new SalutDataReceiver((Activity) mContext, this);
        SalutServiceData mServiceData = new SalutServiceData("server", PORT, mList.get(0).getName());
        mSalut = new Salut(mDataReceiver, mServiceData, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("The mobile cannot support the Wifi~connect~ T>T");
                Toast.makeText(mContext,"The mobile cannot support the Wifi~connect~ T>T",Toast.LENGTH_SHORT).show();
            }
        });

        mSalut.startNetworkService(new SalutDeviceCallback() {
            @Override
            public void call(SalutDevice salutDevice) {
                Logger.d("startNetworkService, onWifiDeviceConnected, device:" + salutDevice.deviceName);
                Toast.makeText(mContext, "startNetworkService, onWifiDeviceConnected, device:" + salutDevice.deviceName, Toast.LENGTH_SHORT).show();
                if(mColorList.size() != 0){
                    ClientPlayer player = new ClientPlayer(salutDevice.deviceName, mColorList.remove(randomGen.nextInt(mColorList.size())), salutDevice);
                    switch (player.getColor()){
                        case Color.BLUE:
                            player.setName(ConfigueActivity.PLAYER_1);
                            break;
                        case Color.YELLOW:
                            player.setName(ConfigueActivity.PLAYER_2);
                            break;
                        case Color.RED:
                            player.setName(ConfigueActivity.PLAYER_3);
                            break;
                        case Color.GREEN:
                            player.setName(ConfigueActivity.PLAYER_4);
                            break;
                    }
                    player.setRuler(ServerRuler.this);
                    mList.add(player);
                }else{
                    ClientPlayer player = new ClientPlayer(salutDevice.deviceName, Color.NONE, salutDevice);
                    player.setRuler(ServerRuler.this);
                    mEyeList.add(player);
                }
                EventBus.getDefault().post(new UpdateGameInfoEvent("比赛人数:" + mList.size() + "观看人数:" + mEyeList.size()));
            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("startNetworkService, init success");
                Toast.makeText(mContext,"startNetworkService, init success~",Toast.LENGTH_SHORT).show();
            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("startNetworkService, init failed");
                Toast.makeText(mContext,"startNetworkService, init failed~",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void uninit(){
        Toast.makeText(mContext,"uninit~",Toast.LENGTH_SHORT).show();

        currentPlayer.exit();//离开的时候给个消息通知下呗。

        if (mSalut.isRunningAsHost) {
            mSalut.stopNetworkService(false);
        } else {
            mSalut.unregisterClient(false);
        }
    }

    @Override
    public void start() {
        Logger.d("The Game is Start~");

        for(Player player:mList){
            player.start(player.getColor());
        }

        for(Player player:mWinnerList){
            player.start(player.getColor());
        }

        for(Player player:mEyeList){
            player.start(Color.NONE);
        }

        currentPlayer.onYourTurn(true,currentPlayer.getName() + "摇骰子~啦");//hiahiahia~我也觉得自己好白痴。为毛能写这么久
    }

    @Override
    public void restart() {
        stepList.clear();
        currentPlayer = localPlayer;

        mList.addAll(mWinnerList);
        mWinnerList.clear();

        for(Player player:mList){
            player.restart();
        }

        for(Player player:mEyeList){
            player.restart();
        }

        isStart = true;
        currentPlayer.onYourTurn(true,currentPlayer.getName() + "摇骰子~啦");//hiahiahia~我也觉得自己好白痴。为毛能写这么久
    }

    public void myTurn(Boolean isTurn,String content){//第一次写~灰常尴尬。为了满足我的强迫症，足足琢磨了半天。hiahiahia~
        if(isTurn) isYourTurn = true;//大兄弟是你的机会鸟~
        EventBus.getDefault().post(new UpdateDiceEvent(0));
        EventBus.getDefault().post(new UpdateTitleEvent(content));
    }

    public void nextPalyer() {
        if(currentPlayer == null){
            currentPlayer = mList.get(0);
            return;
        }
        int currentIndex = mList.indexOf(currentPlayer) + 1;
        if(mWinnerList.size() != 0)
            mList.remove(mWinnerList.get(mWinnerList.size()-1));

        if(mList.size() <= 1) {
            Logger.d(mWinnerList.toString());
            EventBus.getDefault().post(mWinnerList);
            isStart = false;
            return;
        }
        Logger.d(currentIndex + ": current");
        currentPlayer = (currentIndex != mList.size() ? mList.get(currentIndex) : mList.get(0));
    }

    private final Random randomGen = new Random();

    @Override
    public void dice() {
        if(isStart){
            if(isYourTurn){
                random = randomGen.nextInt(6) + 1;
                if (isAllCanNotFly(currentPlayer.getColor())){
                    //通知下一位选手准备~
                    EventBus.getDefault().post(new UpdateDiceEvent(random));
                    EventBus.getDefault().post(new UpdateTitleEvent(currentPlayer.getName() + "摇到的点数为：" + random));

                    for(Player player:mList){
                        if(player != currentPlayer)//就自己不调用~
                            player.think(random);
                    }

                    for(Player player:mWinnerList){
                        player.think(random);
                    }

                    for(Player player:mEyeList){
                        player.think(random);
                    }

                    nextPalyer();

                    for(Player player:mList){
                        player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
                    }

                    for(Player player:mWinnerList){
                        player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
                    }

                    for(Player player:mEyeList){
                        player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
                    }

                }else{//正常的逻辑~
                    for(Player player:mList){
                        player.think(random);
                    }

                    for(Player player:mWinnerList){
                        player.think(random);
                    }

                    for(Player player:mEyeList){
                        player.think(random);
                    }
                }
                isYourTurn = false;
            }
        }
    }

    public boolean isAllCanNotFly(int color) {
        if (color == Color.BLUE) {
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(1) == ChessBoard.TAG_BLUE_BASE_1) && (ChessBoard.planeNum.get(2) == ChessBoard.TAG_BLUE_BASE_2)
                    && (ChessBoard.planeNum.get(3) == ChessBoard.TAG_BLUE_BASE_3) && (ChessBoard.planeNum.get(4) == ChessBoard.TAG_BLUE_BASE_4)) {
                return true;
            }
        }
        if (color == Color.YELLOW) {
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(5) == ChessBoard.TAG_YELLOW_BASE_1) && (ChessBoard.planeNum.get(6) == ChessBoard.TAG_YELLOW_BASE_2)
                    && (ChessBoard.planeNum.get(7) == ChessBoard.TAG_YELLOW_BASE_3) && (ChessBoard.planeNum.get(8) == ChessBoard.TAG_YELLOW_BASE_4)) {
                return true;
            }
        }
        if (color == Color.RED) {
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(9) == ChessBoard.TAG_RED_BASE_1) && (ChessBoard.planeNum.get(10) == ChessBoard.TAG_RED_BASE_2)
                    && (ChessBoard.planeNum.get(11) == ChessBoard.TAG_RED_BASE_3) && (ChessBoard.planeNum.get(12) == ChessBoard.TAG_RED_BASE_4)) {
                return true;
            }
        }
        if (color == Color.GREEN) {
            if (random != Constants.CAN_FLY && (ChessBoard.planeNum.get(13) == ChessBoard.TAG_GREEN_BASE_1) && (ChessBoard.planeNum.get(14) == ChessBoard.TAG_GREEN_BASE_2)
                    && (ChessBoard.planeNum.get(15) == ChessBoard.TAG_GREEN_BASE_3) && (ChessBoard.planeNum.get(16) == ChessBoard.TAG_GREEN_BASE_4)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handle(int planeTag, int theSelectedPlaneTag) {
        //TODO:这里写的是...逻辑上的控制~反馈给player~让它自己去控制棋盘~这样做是因为...想把联网的和本地的写一块~
        //这里的效果其实就是~。所以的棋子都..放下了他们邪恶的棋子~
        Step step = getStep(planeTag, theSelectedPlaneTag);


        //random = 0;
        //EventBus.getDefault().post(new UpdateDiceEvent(0));
        stepList.add(step);

        for (Player player:mList){
            player.putChess(step);
        }

        for(Player player:mWinnerList){
            player.putChess(step);
        }

        for(Player player:mEyeList){
            player.putChess(step);
        }

        if(!isEnd){
            if (random != Constants.CAN_FLY) {       //摇到6就奖励一次//这个逻辑我很满意~233
                nextPalyer();
            }
            for(Player player:mList){
                player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
            }

            for(Player player:mWinnerList){
                player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
            }

            for(Player player:mEyeList){
                player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
            }
        }else{//如果结束的话~就给大家发送结束的~通知呗。就是这个样子~
            for(Player player:mList){
                player.end(mWinnerList);
            }

            for(Player player:mWinnerList){
                player.end(mWinnerList);
            }

            for(Player player:mEyeList){
                player.end(mWinnerList);
            }
        }
    }

    private Step getStep(int planeTag, int theSelectedPlaneTag) {
        Step step = null;
        switch (currentPlayer.getColor()){
            case Color.BLUE:
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
                                }
                            }
                        }
                        else
                            step = new Step(planeTag,ChessBoard.TAG_BLUE_END * 2 - (theSelectedPlaneTag + random));
                    }
                }
                break;
            case Color.YELLOW:
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
                                }
                            }
                        } else {
                            step = new Step(planeTag, ChessBoard.TAG_YELLOW_END * 2 - (theSelectedPlaneTag + random));
                        }
                    }
                }
                break;
            case Color.GREEN:
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
                                }
                            }
                        } else {
                            step = new Step(planeTag, ChessBoard.TAG_GREEN_END * 2 - (theSelectedPlaneTag + random));
                        }
                    }
                }
                break;
            case Color.RED:
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
                                }
                            }
                        } else {
                            step = new Step(planeTag, ChessBoard.TAG_RED_END * 2 - (theSelectedPlaneTag + random));
                        }
                    }
                }
                break;
        }
        return step;
    }

    @Override
    public void onDataReceived(Object data) {
        //TODO:在各个设备之间定义一套通信协议，初步有控制字段:开始，结束，谁赢谁输.，planeTag,shapeTag..还是要好好设计一下。
        String str = (String) data;
        try {
            Message message = LoganSquare.parse(str, Message.class);
            Toast.makeText(mContext,message.toString(),Toast.LENGTH_LONG);
            int type = message.mMessageType;
            switch (type) {
                case Message.MSG_TYPE_HOST_BEGIN:
                    break;
                case Message.MSG_TYPE_BEGIN_ACK:
                    break;
                case Message.MSG_TYPE_GAME_DATA:
                    break;
                case Message.MSG_TYPE_GAME_END:
                    break;
                case Message.MSG_TYPE_GAME_RESTART_REQ:
                    break;
                case Message.MSG_TYPE_GAME_RESTART_RESP:
                    break;
                case Message.MSG_TYPE_MOVE_BACK_REQ:
                    break;
                case Message.MSG_TYPE_MOVE_BACK_RESP:
                    break;
                case Message.MSG_TYPE_EXIT:
                    Toast.makeText(mContext, message.mMessage + "退出了~", Toast.LENGTH_LONG);
                    break;
                case Message.MSG_TYPE_DICE://收到了摇骰子的信号，故来摇一个骰子~
                    for(Player player:mList){
                        if(player instanceof ClientPlayer/* && currentPlayer != player*/)//只有发信息的人，以及本机用户不消息噢~
                            player.think(message.mRandom);
                    }

                    for(Player player:mWinnerList){
                        if(player instanceof ClientPlayer/* && currentPlayer != player*/)//只有发信息的人，以及本机用户不消息噢~
                            player.think(message.mRandom);
                    }

                    for(Player player:mEyeList){
                        if(player instanceof ClientPlayer/* && currentPlayer != player*/)//只有发信息的人，以及本机用户不消息噢~
                            player.think(message.mRandom);
                    }

                    random = message.mRandom;

                    EventBus.getDefault().post(new UpdateDiceEvent(message.mRandom));
                    EventBus.getDefault().post(new UpdateTitleEvent(mContext.getString(R.string.dicing) + message.mRandom));

                    if (isAllCanNotFly(message.mChessColor)){//如果对方都还不能起飞~那我们就忽略它吧~╮(╯▽╰)╭//这里本不应该这么做。
                        nextPalyer();
                        for(Player player:mList){
                            player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
                        }

                        for(Player player:mWinnerList){
                            player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
                        }

                        for(Player player:mEyeList){
                            player.onYourTurn(currentPlayer == player,currentPlayer.getName() + "摇骰子啦~");
                        }
                    }
                    break;
                case Message.MSG_TYPE_SELECT_PLANE:
                    handle(message.mPlaneTag,message.mTheSelectedPlaneTag);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Salut getmSalut() {
        return mSalut;
    }
}