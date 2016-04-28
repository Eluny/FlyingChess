package com.project.flyingchess.player;

import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.ruler.ServerRuler;
import com.project.flyingchess.widget.ChessBoard;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2016/4/11.
 */
public class LocalPalayer extends Player implements ChessBoard.OnSelectPlaneListener{
    private ChessBoard chessBoard;

    public LocalPalayer(String name, int color, ChessBoard chessBoard) {
        super(name,color);
        this.chessBoard = chessBoard;
    }

    @Override
    public void think(int random) {
        EventBus.getDefault().post(new UpdateDiceEvent(random));
        EventBus.getDefault().post(new UpdateTitleEvent(getName() + "摇到的点数为：" + random));

        chessBoard.setSelectFromColor(getColor());
        chessBoard.setOnSelectPlaneListener(this);//强行上...棋盘~
    }

    @Override
    public void putChess(Step step) {
        chessBoard.moveChess(step.getPlaneTag(),step.getShapeTag());
    }

    @Override
    public void OnSelectPlane(int planeTag, int theSelectedPlaneTag) {
        getRuler().handle(planeTag,theSelectedPlaneTag);
    }

    @Override
    public void onYourTurn() {
        EventBus.getDefault().post(new UpdateDiceEvent(0));
        EventBus.getDefault().post(new UpdateTitleEvent(getName() + "摇骰子~啦"));
    }

    @Override
    public void onYourTurn(boolean isYourTurn,String content) {
        ((ServerRuler)getRuler()).myTurn(isYourTurn,content);
    }

    @Override
    public void end(List<Player> mWinnerList) {
        EventBus.getDefault().post(mWinnerList);
    }

    @Override
    public void restart() {
        chessBoard.restart();
    }
}
