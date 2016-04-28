package com.project.flyingchess.player;

import com.project.flyingchess.eventbus.AIEvent;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.model.Step;
import com.project.flyingchess.other.Constants;
import com.project.flyingchess.widget.ChessBoard;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2016/4/11.
 */
public class AIPalayer extends Player implements ChessBoard.OnSelectPlaneListener{
    private int random;
    private ChessBoard chessBoard;

    public AIPalayer(String name, int color, ChessBoard chessBoard) {
        super(name,color);
        this.chessBoard = chessBoard;
    }

    @Override
    public void think(int random) {
        EventBus.getDefault().post(new UpdateDiceEvent(random));
        EventBus.getDefault().post(new UpdateTitleEvent(getName() + "摇到的点数为：" + random));

        //chessBoard.setSelectFromColor(getColor());
        for (int i = getColor() * 4 + 1; i <= getColor() * 4 + 4; i++) {
            int tmp = ChessBoard.planeNum.get(i);
            //暂时添加
            if ((random != Constants.CAN_FLY) && (tmp == ChessBoard.TAG_BLUE_BASE_1 || tmp == ChessBoard.TAG_BLUE_BASE_2 || tmp == ChessBoard.TAG_BLUE_BASE_3 || tmp == ChessBoard.TAG_BLUE_BASE_4
                    || tmp == ChessBoard.TAG_YELLOW_BASE_1 || tmp == ChessBoard.TAG_YELLOW_BASE_2 || tmp == ChessBoard.TAG_BLUE_BASE_3 || tmp == ChessBoard.TAG_YELLOW_BASE_4
                    || tmp == ChessBoard.TAG_RED_BASE_1 || tmp == ChessBoard.TAG_RED_BASE_2 || tmp == ChessBoard.TAG_RED_BASE_3 || tmp == ChessBoard.TAG_RED_BASE_4
                    || tmp == ChessBoard.TAG_GREEN_BASE_1 || tmp == ChessBoard.TAG_GREEN_BASE_2 || tmp == ChessBoard.TAG_GREEN_BASE_3 || tmp == ChessBoard.TAG_GREEN_BASE_4)) {
                continue;
            }
            //
            if(tmp != ChessBoard.TAG_BLUE_END && tmp != ChessBoard.TAG_RED_END && tmp != ChessBoard.TAG_YELLOW_END && tmp != ChessBoard.TAG_RED_END){
                //chessBoard.setSelectFromColor(Color.NONE);
                OnSelectPlane(i,tmp);
                break;
            }
        }
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

        EventBus.getDefault().post(new AIEvent());
    }

}
