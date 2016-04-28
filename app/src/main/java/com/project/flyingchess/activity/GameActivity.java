package com.project.flyingchess.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.peak.salut.SalutDevice;
import com.project.flyingchess.R;
import com.project.flyingchess.dialog.PeersDialog;
import com.project.flyingchess.dialog.StartGameDialog;
import com.project.flyingchess.dialog.WaitingGameStartDialog;
import com.project.flyingchess.dialog.WaitingPlayerDialog;
import com.project.flyingchess.dialog.WinnerDialog;
import com.project.flyingchess.eventbus.AIEvent;
import com.project.flyingchess.eventbus.GameStartEvent;
import com.project.flyingchess.eventbus.UpdateDiceEvent;
import com.project.flyingchess.eventbus.UpdateGameInfoEvent;
import com.project.flyingchess.eventbus.UpdateTimeEvent;
import com.project.flyingchess.eventbus.UpdateTitleEvent;
import com.project.flyingchess.eventbus.WinnerEvent;
import com.project.flyingchess.player.AIPalayer;
import com.project.flyingchess.player.LocalPalayer;
import com.project.flyingchess.player.Player;
import com.project.flyingchess.ruler.ClientRuler;
import com.project.flyingchess.ruler.DefaultRuler;
import com.project.flyingchess.ruler.IRuler;
import com.project.flyingchess.ruler.ServerRuler;
import com.project.flyingchess.utils.Color;
import com.project.flyingchess.widget.ChessBoard;
import com.project.flyingchess.widget.ShakeLayout;
import com.project.flyingchess.widget.ShakeListener;
import com.project.flyingchess.widget.UpMarqueeTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameActivity extends BaseActivity implements View.OnClickListener,ShakeListener.OnShakeListener,ShakeLayout.AnimListener{
    private static final String TAG = "GameActivity";
    private Context mContext;

    private IRuler ruler;
    private List<Player> mList;

    public static final String KEY_MODE = "MODE";
    private int MODE = SINGLE;
    public static final int SINGLE = 0;
    public static final int NET_SERVER = SINGLE + 1;
    public static final int NET_CLIENT = SINGLE + 2;

    private ChessBoard v_chessBoard;
    private TextView tv_time;
    private UpMarqueeTextView tv_title;

    private WinnerDialog winnerDialog;
    private StartGameDialog startGameDialog;
    private WaitingPlayerDialog waitingPlayerDialog;
    private PeersDialog peersDialog;
    private WaitingGameStartDialog waitingGameStartDialog;

    //private ImageView iv_dice;
    //骰子的部分~
    private ShakeLayout sl_dice;
    private SoundPool soundPool;
    private ShakeListener shakeListener;
    private Handler mHandler = new Handler();
    private HashMap<Integer, Integer> aduioMap = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mContext = this;

        EventBus.getDefault().register(this);

        MODE = getIntent().getIntExtra(KEY_MODE,SINGLE);

        findview();
        init();
    }

    private void init() {
        switch (MODE){
            case SINGLE:
                mList = new ArrayList<>();

                int player_1 = getIntent().getIntExtra(ConfigueActivity.PLAYER_1,SINGLE);
                int player_2 = getIntent().getIntExtra(ConfigueActivity.PLAYER_2,SINGLE);
                int player_3 = getIntent().getIntExtra(ConfigueActivity.PLAYER_3,SINGLE);
                int player_4 = getIntent().getIntExtra(ConfigueActivity.PLAYER_4,SINGLE);

                if(player_1 != ConfigueActivity.NONE)
                    mList.add(judgePlayer(player_1,ConfigueActivity.PLAYER_1,Color.BLUE));
                if(player_2 != ConfigueActivity.NONE)
                    mList.add(judgePlayer(player_2,ConfigueActivity.PLAYER_2,Color.YELLOW));
                if(player_3 != ConfigueActivity.NONE)
                    mList.add(judgePlayer(player_3,ConfigueActivity.PLAYER_3,Color.RED));
                if(player_4 != ConfigueActivity.NONE)
                    mList.add(judgePlayer(player_4,ConfigueActivity.PLAYER_4,Color.GREEN));

                ruler = new DefaultRuler(mContext,mList);
                ruler.start();

                break;
            case NET_SERVER:
            case NET_CLIENT:
                initStartGameDialog();
                initWaitingPlayerDialog();
                initPeersDialog();
                initWaitingStartGameDialog();
                break;
            default:
                Logger.d("莫名其妙的错误啊~亲。 ");
                break;
        }

        initWinnerDialog();
        initDice();
    }

    private void initDice() {
        shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(this);
        sl_dice.setAnimListener(this);
        sl_dice.setOnClickListener(this);

        initAudio();
    }

    private void initAudio() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        aduioMap.put(1, soundPool.load(this, R.raw.rotate, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            }
        });
    }

    private void initWaitingStartGameDialog() {
        waitingGameStartDialog = new WaitingGameStartDialog(GameActivity.this, R.style.NoTitleDialog);
        waitingGameStartDialog.setCanceledOnTouchOutside(false);
    }

    private void initWinnerDialog() {
        winnerDialog = new WinnerDialog(GameActivity.this, R.style.NoTitleDialog);
        winnerDialog.setCanceledOnTouchOutside(false);
        winnerDialog.setRestartListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"restart~",Toast.LENGTH_SHORT).show();
                if(ruler != null) ruler.restart();
                if(v_chessBoard != null) v_chessBoard.restart();
                winnerDialog.dismiss();
            }
        });
    }

    private void initPeersDialog() {
        peersDialog = new PeersDialog(GameActivity.this, R.style.NoTitleDialog);
        peersDialog.setCanceledOnTouchOutside(false);
        peersDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peersDialog.dismiss();
                startGameDialog.show();
            }
        });
    }

    private void initWaitingPlayerDialog() {
        waitingPlayerDialog = new WaitingPlayerDialog(GameActivity.this, R.style.NoTitleDialog);
        waitingPlayerDialog.setCanceledOnTouchOutside(false);
        waitingPlayerDialog.setBeginListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingPlayerDialog.dismiss();
                Toast.makeText(mContext,"begin~",Toast.LENGTH_SHORT).show();
                ruler.start();
            }
        });
        waitingPlayerDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ruler.uninit();
                waitingPlayerDialog.dismiss();
                startGameDialog.show();
            }
        });
    }

    private void initStartGameDialog() {
        startGameDialog = new StartGameDialog(GameActivity.this, R.style.NoTitleDialog);
        startGameDialog.setCanceledOnTouchOutside(false);
        startGameDialog.setCreateGameListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"start game~",Toast.LENGTH_SHORT).show();
                ruler = new ServerRuler(mContext,new LocalPalayer(ConfigueActivity.PLAYER_1,Color.BLUE,v_chessBoard));

                startGameDialog.dismiss();
                waitingPlayerDialog.show();
            }
        });

        startGameDialog.setJoinGameListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"join game~",Toast.LENGTH_SHORT).show();
                //ruler = new ServerRuler(mContext,new LocalPalayer(ConfigueActivity.PLAYER_1,Color.BLUE,v_chessBoard));
                ruler = new ClientRuler(mContext,new LocalPalayer("T>T", Color.NONE, v_chessBoard));

                startGameDialog.dismiss();
                peersDialog.show();
                peersDialog.findPeers();
            }
        });

        startGameDialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameDialog.dismiss();
                finish();
            }
        });

        startGameDialog.show();
    }

    private Player judgePlayer(int num,String name,int color) {
        Player player = null;
        switch (num){
            case ConfigueActivity.HUMAN:
                player = new LocalPalayer(name, color, v_chessBoard);
                break;
            case ConfigueActivity.COMPUTER:
                player = new AIPalayer(name, color, v_chessBoard);
                break;
            /*case ConfigueActivity.NONE:
                player = new NonePlayer(name, Color.NONE);
                break;*/
            default:
                break;
        }
        return player;
    }

    private void findview() {
        v_chessBoard = (ChessBoard) findViewById(R.id.v_chessboard);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_title = (UpMarqueeTextView) findViewById(R.id.tv_title);
        sl_dice = (ShakeLayout) findViewById(R.id.sl_dice);
        //iv_dice = (ImageView) findViewById(R.id.iv_dice);

        //iv_dice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sl_dice:
                sl_dice.setClickable(false);
                sl_dice.anim();
                break;

            default:
                break;
        }
    }

    @Override
    public void onShake() {
        shakeListener.stop();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sl_dice.anim();
                shakeListener.start();
            }
        }, 600);
        play();
    }

    @Override
    public void onAnimFinish() {
        sl_dice.setClickable(true);
        if(ruler != null)
            ruler.dice();
    }

    public void play() {
        if (soundPool != null) {
            soundPool.play(aduioMap.get(1), 1.0f, 1.0f, 0, 0, 1);
        }
    }

    public void stop() {
        if (soundPool != null) {
            soundPool.release();
        }
    }

    @Subscribe
    public void onEventMainThread(UpdateTimeEvent msg) {
        tv_time.setText(msg.getMsgContent());
    }

    @Subscribe
    public void onEventMainThread(UpdateDiceEvent msg) {
        final int random = msg.getNumber();
        switch (msg.getNumber()){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                sl_dice.randomImg(random);
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(WinnerEvent msg) {
        String[] players = msg.getMsgContent().split(",");
        if(players.length >= 1)
            winnerDialog.setFirst(players[0]);
        else
            winnerDialog.setFirst("");

        if(players.length >= 2)
            winnerDialog.setSecond(players[1]);
        else
            winnerDialog.setSecond("");
        if(players.length >= 3)
            winnerDialog.setThird(players[2]);
        else
            winnerDialog.setThird("");
        winnerDialog.setBtnVisible(false);
        winnerDialog.show();
    }

    @Subscribe
    public void onEventMainThread(UpdateTitleEvent msg) {
        tv_title.setText(msg.getMsgContent());
    }

    @Subscribe
    public void onEventMainThread(UpdateGameInfoEvent msg) {
        waitingPlayerDialog.setContent(msg.getMsgContent());
    }

    @Subscribe
    public void onEventMainThread(List<Player> players) {
        if(players.size() >= 1)
            winnerDialog.setFirst(players.get(0).getName());
        else
            winnerDialog.setFirst("");

        if(players.size() >= 2)
            winnerDialog.setSecond(players.get(1).getName());
        else
            winnerDialog.setSecond("");
        if(players.size() >= 3)
            winnerDialog.setThird(players.get(2).getName());
        else
            winnerDialog.setThird("");

        winnerDialog.setBtnVisible(true);
        winnerDialog.show();
    }

    @Subscribe
    public void onEventMainThread(ArrayList<SalutDevice> devices) {
        peersDialog.updateView(devices);
    }

    @Subscribe
    public void onEventMainThread(SalutDevice device) {
        peersDialog.dismiss();
        ((ClientRuler)ruler).setServerDevice(device);//这样写真的好嘛~？
        waitingGameStartDialog.show();
        ruler.start();
    }

    @Subscribe
    public void onEventMainThread(GameStartEvent msg) {
        waitingGameStartDialog.dismiss();
    }

    @Subscribe
    public void onEventMainThread(AIEvent msg) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sl_dice.performClick();
            }
        }, 1500);
    }

    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //TODO~就是不让你退出~怎么样怎么样怎么样~？打我啊.
            //Toast.makeText(mContext,getString(R.string.key_back_slogan),Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        try {
            if(ruler != null) ruler.uninit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(startGameDialog != null)
            startGameDialog.dismiss();

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

    public String getTag() {
        return TAG;
    }
}
