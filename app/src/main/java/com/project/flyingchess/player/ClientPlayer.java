package com.project.flyingchess.player;

import com.orhanobut.logger.Logger;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.SalutDevice;
import com.project.flyingchess.model.Message;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.ruler.ServerRuler;
import com.project.flyingchess.utils.MessageWrapper;

import java.util.List;

/**
 * Created by Administrator on 2016/4/17.
 */
public class ClientPlayer extends Player {
    private SalutDevice mSalutDevice;

    public ClientPlayer(String name, int color, SalutDevice mSalutDevice) {
        super(name, color);
        this.mSalutDevice = mSalutDevice;
    }

    public SalutDevice getmSalutDevice() {
        return mSalutDevice;
    }

    @Override
    public void putChess(Step step) {
        Message message = MessageWrapper.getSendDataMessage(step);
        ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
            }
        });
    }

    @Override
    public void think(int random) {
        Message message = MessageWrapper.getDiceMessage(random,getColor());
        ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
            }
        });
    }

    @Override
    public void onYourTurn(boolean isYourTurn,String content){
        if(isYourTurn){
            Message message = MessageWrapper.getYourTurnMessage();
            ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
                @Override
                public void call() {
                    Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
                }
            });
        }else{
            Message message = MessageWrapper.getNotYourTurnMessage(content);
            ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
                @Override
                public void call() {
                    Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
                }
            });
        }
    }

    @Override
    public void start(int color) {
        Message message = MessageWrapper.getHostBeginMessage(color);
        ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
            }
        });
    }

    @Override
    public void end(List<Player> mWinnerList) {
        StringBuilder sb = new StringBuilder();
        for(Player player:mWinnerList)
            sb.append(player.getName() + ",");
        Message message = MessageWrapper.getGameEndMessage(sb.toString());
        ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
            }
        });
    }

    public void restart(){
        Message message = MessageWrapper.getGameRestartReqMessage();
        ((ServerRuler)getRuler()).getmSalut().sendToDevice(mSalutDevice, message, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("sendToDevice, send data failed :" + mSalutDevice.instanceName);
            }
        });
    }
}
