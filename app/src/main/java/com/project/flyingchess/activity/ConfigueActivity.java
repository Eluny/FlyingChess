package com.project.flyingchess.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.project.flyingchess.R;
import com.project.flyingchess.utils.ACache;

public class ConfigueActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "ConfigueActivity";
    private Context mContext;

    private TextView tv_human_1;
    private TextView tv_human_2;
    private TextView tv_human_3;
    private TextView tv_human_4;
    private TextView tv_computer_1;
    private TextView tv_computer_2;
    private TextView tv_computer_3;
    private TextView tv_computer_4;
    private TextView tv_none_1;
    private TextView tv_none_2;
    private TextView tv_none_3;
    private TextView tv_none_4;

    public static final int HUMAN = 0;
    public static final int COMPUTER = 1;
    public static final int NONE = 2;

    public static final String PLAYER_1 = "蓝色飞机~";
    public static final String PLAYER_2 = "黄色飞机~";
    public static final String PLAYER_3 = "红色飞机~";
    public static final String PLAYER_4 = "绿色飞机~";

    private int player_1,player_2,player_3,player_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configue);

        mContext = this;

        findview();
    }

    private void findview() {
        Button btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_restart = (Button) findViewById(R.id.btn_restart);
        tv_human_1 = (TextView) findViewById(R.id.tv_human_1);
        tv_human_2 = (TextView) findViewById(R.id.tv_human_2);
        tv_human_3 = (TextView) findViewById(R.id.tv_human_3);
        tv_human_4 = (TextView) findViewById(R.id.tv_human_4);
        tv_computer_1 = (TextView) findViewById(R.id.tv_computer_1);
        tv_computer_2 = (TextView) findViewById(R.id.tv_computer_2);
        tv_computer_3 = (TextView) findViewById(R.id.tv_computer_3);
        tv_computer_4 = (TextView) findViewById(R.id.tv_computer_4);
        tv_none_1 = (TextView) findViewById(R.id.tv_none_1);
        tv_none_2 = (TextView) findViewById(R.id.tv_none_2);
        tv_none_3 = (TextView) findViewById(R.id.tv_none_3);
        tv_none_4 = (TextView) findViewById(R.id.tv_none_4);

        btn_start.setOnClickListener(this);
        btn_restart.setOnClickListener(this);
        tv_human_1.setOnClickListener(this);
        tv_human_2.setOnClickListener(this);
        tv_human_3.setOnClickListener(this);
        tv_human_4.setOnClickListener(this);
        tv_computer_1.setOnClickListener(this);
        tv_computer_2.setOnClickListener(this);
        tv_computer_3.setOnClickListener(this);
        tv_computer_4.setOnClickListener(this);
        tv_none_1.setOnClickListener(this);
        tv_none_2.setOnClickListener(this);
        tv_none_3.setOnClickListener(this);
        tv_none_4.setOnClickListener(this);

        tv_human_1.performClick();
        tv_human_2.performClick();
        tv_human_3.performClick();
        tv_human_4.performClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                ACache.get(mContext).put("FlyingChess", " ");

                Logger.d("Single dog~");
                if(player_1 == NONE && player_2 == NONE
                        && player_3 == NONE && player_4 == NONE){
                    Toast.makeText(ConfigueActivity.this,"~没有对象不给玩啊~",Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(ConfigueActivity.this,GameActivity.class)
                    .putExtra(GameActivity.KEY_MODE , GameActivity.SINGLE)
                    .putExtra(PLAYER_1,player_1)
                    .putExtra(PLAYER_2,player_2)
                    .putExtra(PLAYER_3,player_3)
                    .putExtra(PLAYER_4,player_4)
                );
                break;
            case R.id.btn_restart:
                Logger.d("Single dog~");
                if(player_1 == NONE && player_2 == NONE
                        && player_3 == NONE && player_4 == NONE){
                    Toast.makeText(ConfigueActivity.this,"~没有对象不给玩啊~",Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(ConfigueActivity.this,GameActivity.class)
                        .putExtra(GameActivity.KEY_MODE , GameActivity.SINGLE)
                        .putExtra(PLAYER_1,player_1)
                        .putExtra(PLAYER_2,player_2)
                        .putExtra(PLAYER_3,player_3)
                        .putExtra(PLAYER_4,player_4)
                );

                /*try {
                    ACache.get(mContext).put("FlyingChess", LoganSquare.serialize(steps));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.d(ACache.get(mContext).getAsString("FlyingChess"));*/


                break;
            case R.id.tv_human_1:
                player_1 = HUMAN;

                tv_human_1.setSelected(true);
                tv_computer_1.setSelected(false);
                tv_none_1.setSelected(false);

                break;
            case R.id.tv_human_2:
                player_2 = HUMAN;

                tv_human_2.setSelected(true);
                tv_computer_2.setSelected(false);
                tv_none_2.setSelected(false);

                break;
            case R.id.tv_human_3:
                player_3 = HUMAN;

                tv_human_3.setSelected(true);
                tv_computer_3.setSelected(false);
                tv_none_3.setSelected(false);

                break;
            case R.id.tv_human_4:
                player_4 = HUMAN;

                tv_human_4.setSelected(true);
                tv_computer_4.setSelected(false);
                tv_none_4.setSelected(false);

                break;
            case R.id.tv_computer_1:
                player_1 = COMPUTER;

                tv_human_1.setSelected(false);
                tv_computer_1.setSelected(true);
                tv_none_1.setSelected(false);

                break;
            case R.id.tv_computer_2:
                player_2 = COMPUTER;

                tv_human_2.setSelected(false);
                tv_computer_2.setSelected(true);
                tv_none_2.setSelected(false);

                break;
            case R.id.tv_computer_3:
                player_3 = COMPUTER;

                tv_human_3.setSelected(false);
                tv_computer_3.setSelected(true);
                tv_none_3.setSelected(false);

                break;
            case R.id.tv_computer_4:
                player_4 = COMPUTER;

                tv_human_4.setSelected(false);
                tv_computer_4.setSelected(true);
                tv_none_4.setSelected(false);

                break;
            case R.id.tv_none_1:
                player_1 = NONE;

                tv_human_1.setSelected(false);
                tv_computer_1.setSelected(false);
                tv_none_1.setSelected(true);

                break;
            case R.id.tv_none_2:
                player_2 = NONE;

                tv_human_2.setSelected(false);
                tv_computer_2.setSelected(false);
                tv_none_2.setSelected(true);

                break;
            case R.id.tv_none_3:
                player_3 = NONE;

                tv_human_3.setSelected(false);
                tv_computer_3.setSelected(false);
                tv_none_3.setSelected(true);

                break;
            case R.id.tv_none_4:
                player_4 = NONE;

                tv_human_4.setSelected(false);
                tv_computer_4.setSelected(false);
                tv_none_4.setSelected(true);

                break;
            default:
                break;
        }
    }

    public String getTag() {
        return TAG;
    }
}
