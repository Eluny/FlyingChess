package com.project.flyingchess.player;

import com.project.flyingchess.model.Step;
import com.project.flyingchess.widget.ChessBoard;

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
        this.random = random;
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
}
