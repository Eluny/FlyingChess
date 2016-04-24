package com.project.flyingchess.ruler;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.orhanobut.logger.Logger;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.project.flyingchess.R;
import com.project.flyingchess.activity.ConfigueActivity;
import com.project.flyingchess.eventbus.GameStartEvent;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.eventbus.WinnerEvent;
import com.project.flyingchess.model.Message;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.player.Player;
import com.project.flyingchess.utils.Color;
import com.project.flyingchess.utils.MessageWrapper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/4/11.
 */
public class ClientRuler implements IRuler,SalutDataCallback {
    private Context mContext;

    private static int random;
    private List<Step> stepList = new ArrayList<>();//记录棋局的行走情况

    private Salut mSalut;
    private SalutDevice serverDevice;
    private Player currentPlayer;

    private boolean isYourTurn = false;

    public ClientRuler(Context mContext, Player currentPlayer) {
        this.mContext = mContext;
        this.currentPlayer = currentPlayer;

        init();
    }

    @Override
    public void init() {
        currentPlayer.setRuler(this);

        if (!Salut.isWiFiEnabled(mContext)) {
            Salut.enableWiFi(mContext);
        }

        SalutDataReceiver mDataReceiver = new SalutDataReceiver((Activity) mContext, this);
        SalutServiceData mServiceData = new SalutServiceData("server", PORT, currentPlayer.getName());
        mSalut = new Salut(mDataReceiver, mServiceData, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("The mobile cannot support the Wifi~connect~ T>T");
                Toast.makeText(mContext,"The mobile cannot support the Wifi~connect~ T>T",Toast.LENGTH_SHORT).show();
            }
        });

        mSalut.discoverWithTimeout(new SalutCallback() {
            @Override
            public void call() {
                Toast.makeText(mContext,mSalut.foundDevices.toString() + "is founded",Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(mSalut.foundDevices);
            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Toast.makeText(mContext,"Long time no see The Sever Device~",Toast.LENGTH_SHORT).show();
                Logger.d("Long time no see The Sever Device~");
            }
        }, 6000);
    }

    @Override
    public void start() {
        mSalut.registerWithHost(serverDevice, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("registerWithHost, registered success");
                Toast.makeText(mContext,"registerWithHost, registered success",Toast.LENGTH_SHORT).show();
            }
        }, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("registerWithHost, registered failed");
                Toast.makeText(mContext,"registerWithHost, registered failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void restart() {
        currentPlayer.restart();
    }

    @Override
    public void uninit() {
        if (mSalut.isRunningAsHost) {
            mSalut.stopNetworkService(false);
        } else {
            mSalut.unregisterClient(false);
        }
    }

    public void nextPalyer() {
    }

    private final Random randomGen = new Random();

    @Override
    public void dice() {
        if(isYourTurn){
            random = randomGen.nextInt(6) + 1;
            EventBus.getDefault().post(new UpdateDiceEvent(random));

            Toast.makeText(mContext,"摇个骰子~",Toast.LENGTH_LONG).show();

            //告诉大家~你摇了啥~好来个直播嘛。by:hunnny
            Message message = MessageWrapper.getDiceMessage(random,currentPlayer.getColor());
            mSalut.sendToHost(message, new SalutCallback() {
                @Override
                public void call() {
                    Logger.d("sendToHost, send data failed");
                }
            });

            currentPlayer.think(random);
            isYourTurn = false;
        }
    }

    @Override
    public void handle(int planeTag, int theSelectedPlaneTag) {
        Logger.d("planeTag : " + planeTag + " ,theSelectedPlaneTag : " + theSelectedPlaneTag);
        Message message = MessageWrapper.getSelectPlaneMessage(planeTag, theSelectedPlaneTag);
        mSalut.sendToHost(message, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("sendToHost, send data failed");
            }
        });
    }

    @Override
    public void onDataReceived(Object data) {
        //TODO:在各个设备之间定义一套通信协议，初步有控制字段:开始，结束，谁赢谁输.，planeTag,shapeTag..还是要好好设计一下。
        String str = (String) data;
        try {
            Message message = LoganSquare.parse(str, Message.class);
            Toast.makeText(mContext,message.toString(),Toast.LENGTH_LONG).show();;
            int type = message.mMessageType;
            switch (type) {
                case Message.MSG_TYPE_HOST_BEGIN:
                    EventBus.getDefault().post(new UpdateTitleEvent(mContext.getString(R.string.start_game)));
                    EventBus.getDefault().post(new GameStartEvent());
                    currentPlayer.setColor(message.mChessColor);//游戏开始的时候~顺便确认一下用户的骰子~颜色,名字~
                    switch (message.mChessColor){
                        case Color.BLUE:
                            currentPlayer.setName(ConfigueActivity.PLAYER_1);
                            break;
                        case Color.YELLOW:
                            currentPlayer.setName(ConfigueActivity.PLAYER_2);
                            break;
                        case Color.RED:
                            currentPlayer.setName(ConfigueActivity.PLAYER_3);
                            break;
                        case Color.GREEN:
                            currentPlayer.setName(ConfigueActivity.PLAYER_4);
                            break;
                    }
                    break;
                case Message.MSG_TYPE_BEGIN_ACK:
                    break;
                case Message.MSG_TYPE_GAME_DATA:
                    currentPlayer.putChess(message.mGameData);
                    break;
                case Message.MSG_TYPE_GAME_END:
                    EventBus.getDefault().post(new WinnerEvent(message.mMessage));
                    break;
                case Message.MSG_TYPE_GAME_RESTART_REQ:
                    restart();
                    break;
                case Message.MSG_TYPE_GAME_RESTART_RESP:
                    break;
                case Message.MSG_TYPE_MOVE_BACK_REQ:
                    break;
                case Message.MSG_TYPE_MOVE_BACK_RESP:
                    break;
                case Message.MSG_TYPE_EXIT:
                    break;
                case Message.MSG_TYPE_YOUR_TURN:
                    EventBus.getDefault().post(new UpdateDiceEvent(0));
                    EventBus.getDefault().post(new UpdateTitleEvent(mContext.getString(R.string.your_turn)));
                    isYourTurn = true;
                    break;
                case Message.MSG_TYPE_NOT_YOUR_TURN:
                    EventBus.getDefault().post(new UpdateDiceEvent(0));
                    EventBus.getDefault().post(new UpdateTitleEvent(message.mMessage));
                    break;
                case Message.MSG_TYPE_DICE:
                    EventBus.getDefault().post(new UpdateDiceEvent(message.mRandom));
                    EventBus.getDefault().post(new UpdateTitleEvent(mContext.getString(R.string.dicing) + message.mRandom));
                    break;
                case Message.MSG_TYPE_SELECT_PLANE:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setServerDevice(SalutDevice serverDevice) {
        this.serverDevice = serverDevice;
    }
}