package com.project.flyingchess.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.flyingchess.R;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateTimeEvent;
import com.project.flyingchess.eventbus.WinnerEvent;
import com.project.flyingchess.player.Color;
import com.project.flyingchess.player.LocalPalayer;
import com.project.flyingchess.player.Player;
import com.project.flyingchess.ruler.DefaultRuler;
import com.project.flyingchess.ruler.IRuler;
import com.project.flyingchess.widget.ChessBoard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "GameActivity";

    private IRuler ruler;
    private List<Player> mList;

    private ChessBoard v_chessBoard;
    private TextView tv_time,tv_title;
    private ImageView iv_dice;

    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        EventBus.getDefault().register(this);

        findview();
        init();
    }

    private void init() {
        mList = new ArrayList<>();

        mList.add(new LocalPalayer("Player 1", Color.BLUE, v_chessBoard));
        //mList.add(new LocalPalayer("Player 2", Color.RED, v_chessBoard));
        //mList.add(new LocalPalayer("Player 3", Color.YELLOW, v_chessBoard));
        //mList.add(new LocalPalayer("Player 4", Color.GREEN, v_chessBoard));
        //mList.add(new AIPalayer(v_chessBoard));
        //mList.add(new AIPalayer(v_chessBoard));
        //mList.add(new AIPalayer(v_chessBoard));

        ruler = new DefaultRuler(mList);
        ruler.start();
    }

    private void findview() {
        v_chessBoard = (ChessBoard) findViewById(R.id.v_chessboard);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_dice = (ImageView) findViewById(R.id.iv_dice);

        iv_dice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dice:
                ruler.dice();
                break;

            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(UpdateTimeEvent msg) {
        tv_time.setText(msg.getMsgContent());
    }

    @Subscribe
    public void onEventMainThread(UpdateDiceEvent msg) {
        switch (msg.getNumber()){
            case 1:
                iv_dice.setImageResource(R.drawable.d1);
                break;
            case 2:
                iv_dice.setImageResource(R.drawable.d2);
                break;
            case 3:
                iv_dice.setImageResource(R.drawable.d3);
                break;
            case 4:
                iv_dice.setImageResource(R.drawable.d4);
                break;
            case 5:
                iv_dice.setImageResource(R.drawable.d5);
                break;
            case 6:
                iv_dice.setImageResource(R.drawable.d6);
                break;
            default:
                iv_dice.setImageResource(R.drawable.icon_dice);
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(WinnerEvent msg) {
        tv_title.setText(msg.getName() + "已经超神了~");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public String getTag() {
        return TAG;
    }
}
